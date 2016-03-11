import java.util.TreeSet;

/**
 * Finds primes using trial division.
 */
public class PrimeFinder {

	/**
	 * Determines whether the specified number is prime using trial division and
	 * direct search factorization. See <a
	 * href="http://mathworld.wolfram.com/DirectSearchFactorization.html">
	 * http://mathworld.wolfram.com/DirectSearchFactorization.html</a> for more
	 * information.
	 * 
	 * @param number
	 *            value to test if prime
	 * @return <code>true</code> if value is prime
	 */
	public static boolean isPrime(int number) {

		// The only even prime is 2.
		if (number == 2) {
			return true;
		}

		// All other even numbers are not prime, as well as any number
		// less than 2.
		if (number % 2 == 0 || number < 2) {
			return false;
		}

		// Only need to perform trial division up to the square root
		int end = (int) Math.ceil(Math.sqrt(number));

		// Check for any odd divisors.
		for (int i = 3; i <= end; i += 2) {
			if (number % i == 0) {
				return false;
			}
		}

		// Otherwise, no divisors and number is prime.
		return true;
	}

	/**
	 * Returns a collection of all primes found between the start and end values
	 * using trial division. See <a
	 * href="http://mathworld.wolfram.com/DirectSearchFactorization.html">
	 * http://mathworld.wolfram.com/DirectSearchFactorization.html</a> for more
	 * information.
	 * 
	 * @param start
	 *            first value to evaluate if prime
	 * @param number
	 *            of values to evaluate if prime
	 * @return list of all prime numbers found
	 */
	public static TreeSet<Integer> trialDivision(int start, int num) {

		TreeSet<Integer> primes = new TreeSet<Integer>();

		for (int i = start; i <= start + num; i++) {
			if (isPrime(i)) {
				primes.add(i);
			}
		}

		return primes;
	}

	/**
	 * Uses multiple worker threads to find all primes less than or equal to the
	 * maximum value. Both the maximum value and number of threads must be a
	 * positive number greater than or equal to 1.
	 * 
	 * @param max
	 *            - maximum value to consider (must be positive)
	 * @param threads
	 *            - number of worker threads (must be positive)
	 * @return set of prime numbers less than or equal to max
	 * @throws InterruptedException
	 */
	public static TreeSet<Integer> findPrimes(int max, int threads) // max is
																	// max num
																	// checked
																	// against
																	// threads
																	// is num of
																	// threads
																	// used
			throws InterruptedException {

		TreeSet<Integer> primes = new TreeSet<Integer>();

		max = max < 1 ? 1 : max;
		threads = threads < 1 ? 1 : threads;

		PrimeWorker[] workers = new PrimeWorker[threads];

		int chunk = max / threads;
		int remainder = max % threads;
		int last = workers.length - 1;

		assert chunk > 0;
		assert remainder >= 0;

		for (int i = 0; i < last; i++) {
			workers[i] = new PrimeWorker(i * chunk, chunk);
			workers[i].start();
		}
		workers[last] = new PrimeWorker(last * chunk, chunk + remainder);
		workers[last].start();

		for (PrimeWorker worker : workers) {
			worker.join();
			primes.addAll(worker.set);
		}

		return primes;
	}

	private static class PrimeWorker extends Thread {

		private final int start;
		private final int end;
		TreeSet<Integer> set;

		public PrimeWorker(int start, int end) {

			this.start = start;
			this.end = end;
			set = new TreeSet<Integer>();

		}

		@Override
		public void run() {
			set = trialDivision(start, end);
		}

	}
}