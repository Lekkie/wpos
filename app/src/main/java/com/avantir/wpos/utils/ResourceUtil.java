package com.avantir.wpos.utils;

import android.content.Context;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class ResourceUtil {

    public static int getIdByName(Context context, String className, String name) {
        String packageName = context.getPackageName();
        //LogUtil.si(ResourceUtil.class, "packageName = " + packageName);
        //LogUtil.si(ResourceUtil.class, packageName + "." + className + "." + name);
        Class r = null;
        int id = 0;
        Class desireClass = null;

        try {
            r = Class.forName(packageName + ".R");
            Class[] e = r.getClasses();

            for(int i = 0; i < e.length; ++i) {
                if(e[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = e[i];
                    break;
                }
            }

            if(desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }
        } catch (Exception var9) {
            //LogUtil.se(ResourceUtil.class, "Resource file missing，please make sure that the R." + className + "." + name + " resources exist!");
            //LogUtil.se(ResourceUtil.class, "The scan code of sdk2.0.18 will use system service as far as possible，please upgrade the system to try again!");
            //LogUtil.se(ResourceUtil.class, "If you use the SDK native scan service，please make sure the resources file in the old SDK is complete!");
        }

        return id;
    }
}
