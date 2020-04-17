package guru.springframework.sfgpetclinic.services;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class Ball {
//	private static final int ROUND_HALF_UP = ;
//	private static final Logger log = Logger.getLogger("Ball.class");
	public static void main(String[] args) {
		System.out.println(maxBall(25));
	}

	public static int maxBall(int intInitSpeed) {
		// your code
		int maxHeight = 0;
		//    log.info("Starting...");
		// if ( intInitSpeed > 0) {
		maxHeight = calculatemaxHeight(intInitSpeed);
		// }
		//   log.info("Completing...");

		return maxHeight;
	}

	private static int calculatemaxHeight(int intInitSpeed) {
		double time = ((intInitSpeed * 1000) / 3600) / 9.81;
//		log.info("TIME: " + time);

//        if (== new BigDecimal(time).setScale(1, RoundingMode.CEILING).doubleValue()) {
//            time = (int) (Math.ceil(time) * 10);
//        } else {
//            time = (int) (Math.floor(time) * 10);
//        }
		int result = (int) ((new BigDecimal(time).setScale(1, ROUND_HALF_UP).doubleValue()) * 10);
//		log.info("RESULT: " + result);
		return result;

//    .    Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
