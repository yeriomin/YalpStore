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
import android.widget.Gallery;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.task.LoadImageTask;

import java.util.List;

class FullscreenImageAdapter extends ImageAdapter {

    private int screenHeight;

    FullscreenImageAdapter(Context context, List<String> screenshotUrls, int screenWidth, int screenHeight) {
        super(context, screenshotUrls, screenWidth);
        this.screenHeight = screenHeight;
    }

    @Override
    protected LoadImageTask getTask() {
        return new FullscreenLoadImageTask(screenWidth, screenHeight);
    }

    static class FullscreenLoadImageTask extends AdapterLoadImageTask {

        private int screenHeight;

        public FullscreenLoadImageTask(int screenWidth, int screenHeight) {
            super(screenWidth);
            this.screenHeight = screenHeight;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setLayoutParams(new Gallery.LayoutParams(screenWidth, screenHeight));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}
