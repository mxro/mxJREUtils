package mx.jreutils.concurrent;

import java.util.concurrent.ExecutorService;

import mx.gwtutils.concurrent.SimpleExecutor;

public class PoolExecutor implements SimpleExecutor {
	
	private final ExecutorService service;
	
	@Override
	public void execute(Runnable runnable) {
		service.submit(runnable);
	}

	public PoolExecutor(ExecutorService service) {
		super();
		this.service = service;
	}
	
	

}
