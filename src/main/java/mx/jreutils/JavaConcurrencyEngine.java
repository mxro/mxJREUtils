package mx.jreutils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import mx.gwtutils.ConcurrencyEngine;
import mx.gwtutils.ThreadSpace.Step;
import mx.gwtutils.tests.AbstractTimer;

public class JavaConcurrencyEngine extends ConcurrencyEngine {

	public volatile boolean delayed = false;

	volatile boolean timeout = false;
	
	
	/**
	 * @see <a href="http://eyalsch.wordpress.com/2010/07/13/multithreaded-tests/">A utility for multithreaded unit tests</a> 
	 */
	@Override
	public Verifyer runAsync(final Step step) {
		final ArrayList<Throwable> exceptions = new ArrayList<Throwable>(); 
		final Thread t = new Thread() {

			@Override
			public void run() {
				try {
					step.process();	
				} catch (final Throwable _t) {
					exceptions.add(_t);
					throw new RuntimeException(_t);
				}
				
			}
			
		};
		t.start();
		
		final Verifyer v = new Verifyer() {

			@Override
			public void join() {
				try {
					t.join();
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
				for (final Throwable _t : exceptions) {
					throw new RuntimeException(_t);
				}
			}
			
		};
		
		return v;
	}

	@Override
	public void delayTestFinish(final int duration) {
		delayed = true;
		timeout = false;
		
		newTimer(new Runnable() {

			@Override
			public void run() {
				if (delayed) {
					timeout = true;
				}
			}

		}).schedule(duration);

		//System.out.println("wait" +this);
		while (delayed) {
			Thread.yield();
			try {
				Thread.sleep(10); 
			} catch (final InterruptedException e) { 
				throw new RuntimeException(e);
			}
			if (timeout) {
				throw new RuntimeException(new TimeoutException(
				"finishTest() not called in time."));
			}
		}
		
	}

	@Override
	public void finishTest() {
		//System.out.println("finishTest "+this);
		delayed = false;
	}

	@Override
	public AbstractTimer newTimer(final Runnable runnable) {

		return new AbstractTimer() {
			private final Timer timer = new Timer();

			@Override
			public void schedule(final int when) {
				
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						runnable.run();
					}

				}, when);
			}

			@Override
			public void run() {
				runnable.run();
			}

		};
	}

	@Override
	public void yield() {
		Thread.yield();
	}

}