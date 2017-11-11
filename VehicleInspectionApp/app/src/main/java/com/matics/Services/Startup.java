// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.matics.Services;

import com.matics.Utils.AccelerometerTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Referenced classes of package com.mick88.alt.tab:
//            NotificationService

public class Startup extends BroadcastReceiver
{

    public Startup()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context,AccelerometerTracker.class));
    }
}
