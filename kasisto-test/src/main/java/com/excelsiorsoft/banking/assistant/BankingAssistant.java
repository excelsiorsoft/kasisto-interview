package com.excelsiorsoft.banking.assistant;

import static com.excelsiorsoft.banking.assistant.BankingAssistant.ClassPattern.inCaseOf;
import static com.excelsiorsoft.banking.assistant.BankingAssistant.ParsingContext.emptyContext;

import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.excelsiorsoft.banking.assistant.BankingAssistant.DisplayResult.DisplayResultBuilder;
import com.excelsiorsoft.banking.assistant.BankingAssistant.TransferResult.TransferResultBuilder;

/**
 * This is a driver class.  Please:
 * 
 *  <li>run it as a Java app
 *  <li> provide input on the console, i.e.: Can you transfer from my CD 200 dollars to my checking, please?
 *  <li> observe the console output describing flow of operations:
 *  
 *   <pre>
 *   I received your request: 'Can you transfer from my CD 200 dollars to my checking, please?'  
Let me see if I have enough information to start processing it.
TransferContext [request=Can you transfer from my CD 200 dollars to my checking, please?, source=CD, destination=checking, amount=200]
Executing TransferAction withTransferContext [request=Can you transfer from my CD 200 dollars to my checking, please?, source=CD, destination=checking, amount=200]
	logic to obtain source acct id
	logic to obtain id of the destination account
	logic to obtain source acct balance
	logic to validate if transfer is possible
	logic to actually execute money transfer
	logic to query destination account for its current balance
TransferResult [transferParsingContext=TransferContext [request=Can you transfer from my CD 200 dollars to my checking, please?, source=CD, destination=checking, amount=200], sourceAcctNumber=sourceId, sourceAcctAmount=-100.0, destinationAcctNumber=destinationId, destinationAcctAmount=1200.0, isSuccess=true]

 *   </pre>
 * @author Simeon
 *
 */
public class BankingAssistant {


		public static void main(String... args) {
			
			ActionController controller = new ActionController();
			
			try (Scanner scanner = new Scanner(System.in)) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					System.out.println("I received your request: '"+line+"'  \nLet me see if I have enough information to start processing it.");
					controller.produceAction(line);

				}
			}
		
		}
	
	
	/**
	 * Respresents the concept of action to be done in response to particular user input
	 * @author Simeon
	 *
	 */
	public interface Action{
		Result execute(ParsingContext context);
	}
	
	/**
	 * Encapsulates steps involves in transferring money between accounts
	 * @author Simeon
	 *
	 */
	public final static class TransferAction implements Action{

		@Override
		public Result execute(ParsingContext context) {
			System.out.println("Executing TransferAction with" + context);
			TransferContext tContext = (TransferContext) context;
			// actual logic - this is just tentative code (template)
			String sourceId = obtainSourceAcctId(tContext);
			String destId = obtainDestinationAcctId(tContext);
			
			double sourceBalance = obtainSourceAcctBalance(sourceId);
			boolean canTransfer = validate();
			double amountTransfered = canTransfer ? performTransfer(tContext) : 0;
			double destBalance = obtainDestinationAcctBalance(tContext, destId);
			
			TransferResultBuilder builder = TransferResult.builder();
			TransferResult transfer = builder.withTransferParsingContext(tContext)
					.isSuccess(amountTransfered == Double.parseDouble(tContext.amount))
					.withSourceAcctNumber(sourceId)
					.withSourceAcctAmount(sourceBalance - Double.parseDouble(tContext.amount))
					.withDestinationAcctNumber(destId)
					.withDestinationAcctAmount(destBalance).build();
			return transfer;
		}

		private Double obtainDestinationAcctBalance(TransferContext context, String destId) {
			System.out.println("\tlogic to query destination account for its current balance");
			return 1000d+Double.parseDouble(context.amount);
		}

		private double performTransfer(TransferContext context) {
			System.out.println("\tlogic to actually execute money transfer"); 
			return Double.parseDouble(context.amount);
			
		}

		private String obtainDestinationAcctId(TransferContext context) {
			//assuming that it's possible to get the account id based on the user credentials or some other info available outside of TransferContext, i.e. cookies, etc.
			//otherwise will need to pose a question to the user to obtain that context
			System.out.println("\tlogic to obtain id of the destination account");
			return "destinationId";
		}

		private boolean validate() {
			System.out.println("\tlogic to validate if transfer is possible");
			return true;
		}

		private double obtainSourceAcctBalance(String sourceId) {
			System.out.println("\tlogic to obtain source acct balance");
			return 100d;
		}

		private String obtainSourceAcctId(TransferContext context) {
			//assuming that it's possible to get the account id based on the user credentials or some other info available outside of TransferContext, i.e. cookies, etc.
			//otherwise will need to pose a question to the user to obtain that context
			System.out.println("\tlogic to obtain source acct id");
			return "sourceId";
		}
		
	}
	
	/**
	 * Encapsulates steps involved in displaying account balance
	 * @author Simeon
	 *
	 */
	public final static class DisplayAction implements Action{

		@Override
		public Result execute(ParsingContext context) {
			System.out.println("Executing DisplayAction" + context);
			DisplayContext dContext = (DisplayContext) context;
			// actual logic - this is just tentative code (template)
			DisplayResultBuilder builder = DisplayResult.builder();
			
			String targetAcctNumber = obtainTargetAcctNumber();
			DisplayResult display = builder
					.withDisplayParsingContext(dContext)
					.withTargetAcctNumber(targetAcctNumber)
					.withTargetAcctAmount(obtainTargetAccountAmount(targetAcctNumber))
					.isSuccess(true)
					.build();
			return display;
		}

		private double obtainTargetAccountAmount(String acctNum) {
			System.out.println("\tlogic to query target account for its current balance");
			return 800d;
		}

		private String obtainTargetAcctNumber() {
			//assuming that it's possible to get the account id based on the user credentials or some other info available outside of TransferContext, i.e. cookies, etc.
			//otherwise will need to pose a question to the user to obtain that context
			System.out.println("\tlogic to obtain target acct id");
			return "targetAcctNumber";
		}
		
	}
	
	/**
	 * Traffic controller which
	 * <li> accepts the user input
	 * <li> parses it 
	 * <li>dispatches to an appropriate action
	 * @author Simeon
	 *
	 */
	public static final class ActionController{
		
		private final Pattern transferPattern = Pattern.compile(".?([Tt]ransfer|[Ss]end|[Mm]ove).?");
		private final Pattern displayPattern = Pattern.compile(".?([Bb]alance|[Vv]iew|[Ss]how).?");
		
		private ActionMatcher actionMatcher = new ActionMatcher(
			    inCaseOf(TransferContext.class,  new TransferAction()::execute),
			    inCaseOf(DisplayContext.class, context -> new DisplayAction().execute(context))
			);
						
		public void produceAction(String line){
			
			Matcher transferMatcher = transferPattern.matcher(line);
			Matcher displayMatcher = displayPattern.matcher(line);
			
			boolean transfer = transferMatcher.find();
			boolean display = displayMatcher.find();
			
			ParsingContext parsingContext = transfer?new TransferContext(line):display?new DisplayContext(line):emptyContext();
			
			System.out.println(parsingContext);
			Result result = actionMatcher.matchFor(parsingContext);
			System.out.println(result);
		}
	}
	
	/**
	 * Set of pattern matching utilities to generalize dispatch (based on a Java type, etc.)
	 * @author Simeon
	 *
	 */
	public interface _Pattern {
	    boolean matches(Object value);
	    Result apply(Object value);
	}
	
	public static class ActionMatcher {
	    private _Pattern[] patterns;
	 
	    public ActionMatcher(_Pattern... patterns) { this.patterns = patterns; }
	 
	    public Result matchFor(Object value) {
	        for (_Pattern pattern : patterns)
	            if (pattern.matches(value)) return pattern.apply(value);
	 	        throw new IllegalArgumentException("Sorry, no match for " + value);
	    }
	}
	
	public static class ClassPattern<T> implements _Pattern {
		 
	    private Class<T> clazz;
	 
	    private Function<T, Result> function;
	 
	    public ClassPattern(Class<T> clazz, Function<T, Result> function) {
	        this.clazz = clazz;
	        this.function = function;
	    }
	 
	    public boolean matches(Object value) {
	        return clazz.isInstance(value);
	    }
	 
	    @SuppressWarnings("unchecked")
		public Result apply(Object value) {
	        return function.apply((T) value);
	    }
	 
	    public static <T> _Pattern inCaseOf(Class<T> clazz, Function<T, Result> function) {
	        return new ClassPattern<T>(clazz, function);
	    }
	}
	 
	/**
	 * Generalization for the user response
	 * @author Simeon
	 *
	 */
	public static interface Result {

		static Result emptyContext() {
			return new NullResult();
		}
	}
	
	public static final class NullResult implements Result{}
	/**
	 * Response to a {@link TransferAction}
	 * @author Simeon
	 *
	 */
	public static final class TransferResult implements Result{
		
		private TransferResult() {}
		
		public final static class TransferResultBuilder{
			
			private TransferContext transferParsingContext;
			private String sourceAcctNumber;
			private String sourceAcctAmount;
			private String destinationAcctNumber;
			private String destinationAcctAmount;
			private boolean isSuccess;
			
			TransferResultBuilder withTransferParsingContext(TransferContext context) {
				this.transferParsingContext = context;
				return this;
			}
			
			TransferResultBuilder withSourceAcctNumber(String sourceAcctNumber){
				this.sourceAcctNumber = sourceAcctNumber;
				return this;
			}
			
			TransferResultBuilder withSourceAcctAmount(double amt) {
				this.sourceAcctAmount = Double.toString(amt);
				return this;
			}
			
			TransferResultBuilder withDestinationAcctNumber(String acctNum) {
				this.destinationAcctNumber = acctNum;
				return this;
			}
			
			TransferResultBuilder withDestinationAcctAmount(double amt) {
				this.destinationAcctAmount = Double.toString(amt);
				return this;
			}
			
			TransferResultBuilder isSuccess(boolean result) {
				this.isSuccess = result;
				return this;
			}
			
			public TransferResult build() {
				TransferResult result = new TransferResult();
				result.destinationAcctAmount = this.destinationAcctAmount;
				result.destinationAcctNumber = this.destinationAcctNumber;
				result.isSuccess = this.isSuccess;
				result.sourceAcctAmount = this.sourceAcctAmount;
				result.sourceAcctNumber = this.sourceAcctNumber;
				result.transferParsingContext = this.transferParsingContext;
				return result;
			}
		}
		
		private TransferContext transferParsingContext;
		private String sourceAcctNumber;
		private String sourceAcctAmount;
		private String destinationAcctNumber;
		private String destinationAcctAmount;
		private boolean isSuccess;
		
		public static TransferResultBuilder builder() {
			return new TransferResultBuilder();
		}
		
		@Override
		public String toString() {
			return "TransferResult [transferParsingContext=" + transferParsingContext + ", sourceAcctNumber="
					+ sourceAcctNumber + ", sourceAcctAmount=" + sourceAcctAmount + ", destinationAcctNumber="
					+ destinationAcctNumber + ", destinationAcctAmount=" + destinationAcctAmount + ", isSuccess="
					+ isSuccess + "]";
		}
		
	}
	
	/**
	 * Response to a {@link DisplayAction}
	 * @author Simeon
	 *
	 */
public static final class DisplayResult implements Result{
		
		private DisplayResult() {}
		
		public final static class DisplayResultBuilder{
			
			private DisplayContext displayParsingContext;
			private boolean isSuccess;
			private String targetAcctNumber;
			private String targetAcctAmount;
			
			DisplayResultBuilder withDisplayParsingContext(DisplayContext context) {
				this.displayParsingContext = context;
				return this;
			}
			
			DisplayResultBuilder withTargetAcctNumber(String acctNum) {
				this.targetAcctNumber = acctNum;
				return this;
			}
			
			DisplayResultBuilder withTargetAcctAmount(double amt) {
				this.targetAcctAmount = Double.toString(amt);
				return this;
			}

			DisplayResultBuilder isSuccess(boolean result) {
				this.isSuccess = result;
				return this;
			}
			
			public DisplayResult build() {
				DisplayResult result = new DisplayResult();
				
				result.isSuccess = this.isSuccess;
				result.targetAcctAmount = this.targetAcctAmount;
				result.targetAcctNumber = this.targetAcctNumber;
				result.displayParsingContext = this.displayParsingContext;

				return result;
			}
		}
		
		private DisplayContext displayParsingContext;
		private String targetAcctNumber;
		private String targetAcctAmount;
		private boolean isSuccess;
		
		public static DisplayResultBuilder builder() {
			return new DisplayResultBuilder();
		}
		
		@Override
		public String toString() {
			return "DisplayResult [displayParsingContext=" + displayParsingContext + ", targetAcctNumber="
					+ targetAcctNumber + ", targetAcctAmount=" + targetAcctAmount + ", isSuccess=" + isSuccess + "]";
		}
		
	}
	
/**
 * Encapsulates general information required for further action processing
 * @author Simeon
 *
 */
	public interface ParsingContext {

		static ParsingContext emptyContext() {
			return new NullContext();
		}

	}
	
	public static final class NullContext implements ParsingContext{}
	
	/**
	 * Encapsulates data needed for execution of a {@link TransferAction}
	 * @author Simeon
	 *
	 */
	public static final class TransferContext implements ParsingContext{
		
		private final String request;
		private final String source;
		private final String destination;
		private final String amount;
		
		@Override
		public String toString() {
			return "TransferContext [request=" + request + ", source=" + source + ", destination=" + destination
					+ ", amount=" + amount + "]";
		}

		public TransferContext(final String line) {
			this.request = line;
			this.source = parseSource();
			this.destination = parseDesination();
			this.amount = parseAmount();
		}
		
		private String parseSource() {
			String result = "";
			Pattern sourcePattern = Pattern.compile("(?<=from my\\s)(checking|savings|CD)|(?<=from\\s)(checking|savings|CD)");
			Matcher sourceMatcher = sourcePattern.matcher(this.request);
			while (sourceMatcher.find()) {
				
				for (int i = 1; i <= sourceMatcher.groupCount(); i++) {
					String grp = sourceMatcher.group(i);
					if (grp != null)
						result = grp;
				}
			}
			
			return result;

		}

		private String parseDesination() {
			String result = "";
			Pattern destinationPattern = Pattern.compile("(?<=to my\\s)(checking|savings|CD)|(?<=to\\s)(checking|savings|CD)");
			Matcher destinationMatcher = destinationPattern.matcher(this.request);
			while (destinationMatcher.find()) {
				
				for (int i = 1; i <= destinationMatcher.groupCount(); i++) {
					String grp = destinationMatcher.group(i);
					if (grp != null)
						result = grp;
				}
			}
			
			return result;
		}

		private String parseAmount() {
			Pattern amountPattern = Pattern.compile(".?([$]?)(\\d+){1}( dollars)?.?");
			Matcher amountMatcher = amountPattern.matcher(this.request);
			amountMatcher.find();
			return amountMatcher.group(2);
			
		}

	}
	
	/**
	 * Encapsulates data needed for execution of a {@link DisplayAction}
	 * @author Simeon
	 *
	 */
public static final class DisplayContext implements ParsingContext{
		
		private final String request;
		private final String target;

		
		@Override
		public String toString() {
			return "TransferContext [request=" + request + ", target=" + target + "]";
		}

		public DisplayContext(final String line) {
			this.request = line;
			this.target = parseTarget();
			
		}
		
		private String parseTarget() {
			Pattern targetPattern = Pattern.compile(".?([cC]hecking|[sS]avings|[cC][dD]).?");
			Matcher targetMatcher = targetPattern.matcher(this.request);
			targetMatcher.find();
			return targetMatcher.group(1);
		}


	}
	
}
