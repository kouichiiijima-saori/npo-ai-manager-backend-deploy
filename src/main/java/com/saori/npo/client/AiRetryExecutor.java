package com.saori.npo.client;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class AiRetryExecutor {

	public <T> T execute(
			Supplier<T> supplier) {

		RuntimeException lastException = null;

		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				return supplier.get();

			} catch (RuntimeException e) {
				lastException = e;

				if (e instanceof HttpClientErrorException.TooManyRequests) {
					System.out.println("AI API quota exceeded. No retry.");
					throw e;
				}

				System.out.println("AI API retry attempt failed: " + attempt);
				System.out.println(e.getMessage());

				if (attempt < 3) {

					long waitMillis =
							(long) Math.pow(2, attempt - 1) * 6000L;

					System.out.println(
							"AI API retry after "
									+ (waitMillis / 1000)
									+ " sec. attempt="
									+ attempt);

					sleep(waitMillis);
				}
			}
		}

		throw lastException;
	}

	private void sleep(long millis) {

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}