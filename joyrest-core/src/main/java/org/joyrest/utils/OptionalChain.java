package org.joyrest.utils;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalChain<T> {

	private final Optional<T> optional;

	public OptionalChain(Optional<T> optional) {
		this.optional = optional;
	}

	public OptionalChain<T> chainIfEmpty(OptionalChain<T> next) {
		requireNonNull(next);
		return optional.isPresent() ? this : next;
	}

	public T get() {
		return optional.get();
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		return optional.orElseThrow(exceptionSupplier);
	}
}
