/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.chainfire.libsuperuser.Shell;

public class InstallerRoot extends InstallerBackground {

    private App app;

    public InstallerRoot(Context context) {
        super(context);
    }

    @Override
    protected void install(App app) {
        this.app = app;
        List<File> apks = Paths.getApkAndSplits(context, app.getPackageName(), app.getVersionCode());
        if (apks.size() > 1) {
            shellInstall(apks, app.isInstalled());
        } else {
            finish(shellInstall(apks.get(0)));
        }
    }

    private void finish(boolean success) {
        postInstallationResult(app, success);
        if (success) {
            InstallationState.setSuccess(app.getPackageName());
        } else {
            InstallationState.setFailure(app.getPackageName());
            sendFailureBroadcast(app.getPackageName());
        }
    }

    private boolean shellInstall(File file) {
        List<String> lines = Shell.SU.run("pm install -i \"" + BuildConfig.APPLICATION_ID + "\" -r " + file.getAbsolutePath());
        if (null != lines) {
            for (String line: lines) {
                Log.i(getClass().getSimpleName(), line);
            }
        }
        return null != lines && lines.size() == 1 && lines.get(0).equals("Success");
    }

    private void shellInstall(List<File> apks, boolean update) {
        try {
            new InstallCreate(new Shell.Builder().useSU().open()).setApks(apks).setUpdate(update).run();
        } catch (Throwable e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            finish(false);
        }
    }

    private class InstallCommit extends Command {

        private long sessionId;

        public InstallCommit(Shell.Interactive shell) {
            super(shell);
        }

        public InstallCommit setSessionId(long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        @Override
        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
            super.onCommandResult(commandCode, exitCode, output);
            if (exitCode == 0) {
                finish(true);
            }
        }

        @Override
        protected String[] getCommands() {
            return new String[] {"pm install-commit " + sessionId};
        }
    }

    private class InstallWrite extends Command {

        private List<File> apks;
        private long sessionId;

        public InstallWrite(Shell.Interactive shell) {
            super(shell);
        }

        public InstallWrite setApks(List<File> apks) {
            this.apks = apks;
            return this;
        }

        public InstallWrite setSessionId(long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        @Override
        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
            super.onCommandResult(commandCode, exitCode, output);
            new InstallCommit(shell).setSessionId(sessionId).run();
        }

        @Override
        protected String[] getCommands() {
            String[] commands = new String[apks.size()];
            for (int n = 0; n < apks.size(); n++) {
                File apk = apks.get(n);
                commands[n] = "pm install-write -S " + apk.length() + " " + sessionId + " " + apk.getName() + " " + apk.getAbsolutePath();
            }
            return commands;
        }
    }

    private class InstallCreate extends Command {

        private List<File> apks;
        private boolean update;

        public InstallCreate(Shell.Interactive shell) {
            super(shell);
        }

        public InstallCreate setApks(List<File> apks) {
            this.apks = apks;
            return this;
        }

        public InstallCreate setUpdate(boolean update) {
            this.update = update;
            return this;
        }

        @Override
        protected String[] getCommands() {
            long sessionSize = 0;
            for (File apk: apks) {
                sessionSize += apk.length();
            }
            return new String[] {"pm install-create" + (update ? " -r" : "") + " -S " + sessionSize + " -i \"" + BuildConfig.APPLICATION_ID + "\""};
        }

        @Override
        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
            super.onCommandResult(commandCode, exitCode, output);
            long sessionId = getSessionId(output);
            if (sessionId > 0) {
                new InstallWrite(shell).setApks(apks).setSessionId(sessionId).run();
            }
        }

        private long getSessionId(List<String> output) {
            Pattern sessionIdPattern = Pattern.compile(".*(\\[(\\d+)\\])");
            for (String line: output) {
                Matcher matcher = sessionIdPattern.matcher(line);
                if (matcher.matches()) {
                    return Long.parseLong(matcher.group(2));
                }
            }
            return 0;
        }
    }

    private abstract class Command implements Shell.OnCommandResultListener {

        protected Shell.Interactive shell;

        abstract protected String[] getCommands();

        public Command(Shell.Interactive shell) {
            this.shell = shell;
        }

        public void run() {
            String[] commands = getCommands();
            for (String command: commands) {
                Log.i("Shell # ", command);
            }
            shell.addCommand(commands, 0, this);
        }

        @Override
        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
            Log.i("Shell ", "Exit code " + exitCode);
            for (String line: output) {
                Log.i("Shell ", line);
            }
            if (exitCode > 0) {
                finish(false);
            }
        }
    }
}
