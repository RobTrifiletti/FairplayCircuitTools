import java.io.File;
import java.util.List;


public class Driver {

	private static final String FAIRPLAY_CONVERT_TO_CUDA = "-fc";
	private static final String AUG_CHECKSUM = "-ac";
	private static final String FAIRPLAY_EVALUATOR = "-fe";
	private static final String CUDA_EVALUATOR = "-ce";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File inputFile = null;
		File circuitFile = null;
		File outputFile = null;
		boolean timed = false;
		boolean verify = true; //Debug purposes, change to true to check AES on known output
		boolean sorted = false;
		
		String operation = args[0];
		//-fc circuitfile outputfile
		if (operation.equals(FAIRPLAY_CONVERT_TO_CUDA) && checkArgs(args, 3)){
			circuitFile = new File(args[1]);
			outputFile = new File(args[2]);
			FairplayCircuitParser circuitParser = new FairplayCircuitParser(circuitFile);
			FairplayCircuitConverter circuitConverter = new FairplayCircuitConverter(
					circuitParser, outputFile, timed, sorted);
			circuitConverter.run();
			
		}
		//-ac circuitfile outputfile
		else if (operation.equals(AUG_CHECKSUM) && checkArgs(args, 3)){
			checkArgs(args, 3);
			circuitFile = new File(args[1]);
			outputFile = new File(args[2]);
			
			FairplayCircuitParser circuitParser = new FairplayCircuitParser(circuitFile);
			FairplayCircuitAugChecksum ac = new FairplayCircuitAugChecksum(circuitParser, 
					outputFile);
			ac.run();
			
		}
		
		//-fe inputfile circuitfile outputfile
		else if (operation.equals(FAIRPLAY_EVALUATOR) && checkArgs(args, 4)){
			
			inputFile = new File(args[1]);
			circuitFile = new File(args[2]);
			outputFile = new File(args[3]);
			
			FairplayCircuitParser circuitParser = 
					new FairplayCircuitParser(circuitFile);
			FairplayCircuitConverter circuitConverter = 
					new FairplayCircuitConverter(circuitParser, outputFile, 
							false, false);
			List<Gate> gates = circuitParser.getGates();
			List<List<Gate>> layersOfGates = 
					circuitConverter.getLayersOfGates(gates);
			
			CircuitEvaluator eval = 
					new CircuitEvaluator(inputFile, outputFile, layersOfGates, 
							circuitParser.getCUDAHeader(layersOfGates), verify);
			eval.run();
		}
		//-ge inputfile circuitfile outputfile
		else if (operation.equals(CUDA_EVALUATOR) && checkArgs(args, 4)){
			
			inputFile = new File(args[1]);
			circuitFile = new File(args[2]);
			outputFile = new File(args[3]);
			CUDACircuitParser circuitParser = new CUDACircuitParser(circuitFile);
			CircuitEvaluator eval = new CircuitEvaluator(
					inputFile, outputFile, circuitParser.getGates(), 
					circuitParser.getCUDAHeader(), verify);
			eval.run();
		}
		else {
			System.out.println(
					"Your request could not be identified, please " +
					"use one of the following prefixes for your invoke:");
			System.out.println(FAIRPLAY_CONVERT_TO_CUDA + ": Fairplay to CUDA format");
			System.out.println(AUG_CHECKSUM + ": Fairplay checkum augmentation");
			System.out.println(FAIRPLAY_EVALUATOR + ": Fairplay evaluation");
			System.out.println(CUDA_EVALUATOR + ": CUDA evaluation");
		}
				
		

//		for(int param = 0; param < args.length; param++){
//			if (inputFilename == null) {
//				inputFilename = args[param];
//			}
//			else if (circuitFilename == null){
//				circuitFilename = args[param];
//			}
//			else if (outputFilename == null) {
//				outputFilename = args[param];
//			}
//			else if (args[param].equals("-f")){
//				parseStrategy = 
//						new FairplayCompilerParseImpl<Gate>(circuitFilename);
//			}
//			else if (args[param].equals("-v")){
//				verify = true;
//			}
//
//			else System.out.println("Unparsed: " + args[param]); 
//		}
//
//	}
	}


	private static boolean checkArgs(String[] args, int expectedNumberOfArgs) {
		if(args.length != expectedNumberOfArgs){
			System.out.println("Incorrect number of argumens, expected: " + 
		expectedNumberOfArgs);
			return false;
		}
		else return true;
		
	}
}
