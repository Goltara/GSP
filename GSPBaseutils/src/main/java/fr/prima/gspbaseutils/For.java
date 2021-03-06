/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author twilight
 */
public class For extends AbstractModuleEnablable {

    @ModuleParameter(initOnly = true)
    public long warmup = 0;

    @ModuleParameter(change = "periodChanged")
    public long period = 0;

    @ModuleParameter(initOnly = true)
    public int from = 0;

    @ModuleParameter(initOnly = true)
    public int to = -1;

    @ModuleParameter(initOnly = true)
    public int step = 1;

    @ModuleParameter
    public boolean emitEmptyAfter = true;
    /**
     * Should the assembly be shut down when the For ends?
     */
    @ModuleParameter
    public boolean stopAtEnd = true;

    @ModuleParameter
    public boolean forceQuitAtEnd = false;

    // this will be automatically injected by the framework after setup and before init
    @ModuleParameter(initOnly = true)
    public Assembly assembly;

    //
    private int count = 0;
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    Timer timer = null;

    @Override
    protected synchronized void setEnabled(boolean enabled) {
        boolean old = this.enabled;
        super.setEnabled(enabled);
        if (old == this.enabled) {
            return;
        }
        if (this.enabled) {
            startTimer();
        } else {
            timer.cancel();
        }
    }

    public synchronized void periodChanged() {
        if (timer != null) {
            warmup = 10;
            startTimer();
        }
    }

    @Override
    protected synchronized void initModule() {
        count = from;
        assembly.addPostInitHook(new Runnable() {
            @Override
            public void run() {
                startTimer();
            }
        });
    }
    private synchronized void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("Timer Thread for the Clock GSP Module in Baseutils");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                output();
            }
        }, warmup, period);
    }

    @Override
    protected synchronized void stopModule() {
        timer.cancel();
    }

    public synchronized void interrupt() {
        interrupted.set(true);
    }

    private synchronized void output() {
        if (emitEmptyAfter) {
            s();
            i();
            f();
        }
        emitEvent();
        if (!emitEmptyAfter) {
            s();
            i();
            f();
        }
        count += step;
        if (interrupted.get() || (to != -1 && count >= to)) {
            timer.cancel();
            if (stopAtEnd) {
                assembly.stop();
            }
            if (forceQuitAtEnd) {
                System.exit(0);
            }
        }
    }

    private void s() {
        emitEvent("" + count);
    }

    private void i() {
        emitEvent(count);
    }

    private void f() {
        emitEvent((float) count);
    }

}
