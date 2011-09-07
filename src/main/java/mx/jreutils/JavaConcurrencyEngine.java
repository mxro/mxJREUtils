package mx.jreutils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import mx.gwtutils.ConcurrencyEngine;
import mx.gwtutils.tests.AbstractTimer;

public class JavaConcurrencyEngine extends ConcurrencyEngine {

	public volatile boolean delayed = false;

	@Override
	public void delayTestFinish(final int duration) {
		delayed = true;

		newTimer(new Runnable() {

			@Override
			public void run() {
				if (delayed) {
					throw new RuntimeException(new TimeoutException(
							"finishTest() not called in time."));
				}
			}

		}).schedule(duration);

		
		while (delayed) {
			Thread.yield();
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) { 
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void finishTest() {
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