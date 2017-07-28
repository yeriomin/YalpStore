package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.HashSet;
import java.util.Set;

public class SelfSignatureChecker {

    static private final String SIGNATURE_FDROID = "3082035f30820247a003020102020468ba885b300d06092a864886f70d01010b05003060310b300906035504061302554b310c300a060355040813034f5247310c300a060355040713034f524731133011060355040a130a6664726f69642e6f7267310f300d060355040b13064644726f6964310f300d060355040313064644726f6964301e170d3136313233303232303834325a170d3434303531373232303834325a3060310b300906035504061302554b310c300a060355040813034f5247310c300a060355040713034f524731133011060355040a130a6664726f69642e6f7267310f300d060355040b13064644726f6964310f300d060355040313064644726f696430820122300d06092a864886f70d01010105000382010f003082010a0282010100b10ec12303ee581f42f98fab7a31849c7fac9bd3e222b738c12b0fbfcb3d2b50589381da7c4ff42d8c412806188ea6806e0f54595afc651696d21053f89a4dae42ac02a469c8828a5ffc49954c60f9ccc66d60f0863928e0d2b17f8e9103a11d6056c53935abd64c984c3e48dc4611efa2bea89ac48bdb8e1257f23b193567262dea3b39bdc1fb4cf6852155f44920aee08d0dbc458cd43c24f4262ac6f293d88b51b7c7443c321ad77619e270f427fee8109772566aa998ba927c9ed2dc4c48b517c1b37fe1c65a8c1681a542fda60182cf3fb600f8584668815a4bceda81e708a2c815dd85abbabe88cc5719f8a5326284bafb5c4121596bd67f45ed7ec5630203010001a321301f301d0603551d0e04160414e3e2d2155bd13aba9aad5a851634f16e020abb64300d06092a864886f70d01010b05000382010100a431e1424afa29a50bb2ecdf710aa757f09835b7dcd484f2d7738ef58b1dc5928080c885a0082520b248e940b5c561b5bf7f49ea1436bd2d659f4eb432d2743a43bb0756ee236fa17bd4e77a00cd995d0f769d1ab9012382d960f04c8b920cb4c90bc14aa9a93de97387ef00abd86101b4b0be5b50670ff6d271d7719f044e541acaad219a8e02bf714ec5f27a1c2d81bc33a3feaa55cfa53a6488b3b057e97e66545741e41a5194f25f5837c639b190287c47feb6f6c88adcc222f7cdd3bd55d45f79d3f212c547ba9e2f24d286824af6eec7359adba5bf03e1e5b40e47dad059b799105c58a57a3142d7c7f270c9728a84f1992fb4a30da4cbe74803079221";

    static private Set<String> currentSignatures;

    static public boolean isFdroid(Context context) {
        if (null == currentSignatures) {
            currentSignatures = getSignatureSet(context);
        }
        return currentSignatures.size() == 1 && currentSignatures.contains(SIGNATURE_FDROID);
    }

    static private Set<String> getSignatureSet(Context context) {
        Set<String> signatures = new HashSet<>();
        try {
            for (Signature signature: context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES).signatures) {
                signatures.add(signature.toCharsString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Unlikely
        }
        return signatures;
    }
}
