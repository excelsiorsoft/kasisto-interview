## This is a take home programming exercise given by Kasisto

**Problem**

We would like you to implement a “mini-banking-assistant” that can do 2 things:

1 - Responds inquiries about the balance of the different accounts (checking, savings, CD, etc.) that a user has

2 - Transfers money from one user account to another one (for example, from checking to savings).

For example, the system would have to understand sentences such as (but it shouldn’t be limited to just these sentences, other similar sentences should work too):

- What is the balance of my checking account?

- Savings account balance please.

- Transfer $20 from checking to savings please.

- Can you transfer from my CD 200 dollars to my checking, please?

- Please send from savings to checking 45.

Your program should take input from the console and print out to the console the relevant information in the user request (so that we can tell what the program understood). You don’t need to perform any action other than that (that is, there is no need to keep track of accounts and balances, or how they are affected after a transfer —all we care is what the program understands from the user input).

We do not expect this to be a full fledged banking assistant, so we do not expect it will understand every possible way in which a user may pose some of these requests. All we expect is that it understands *some* variation of user inputs (you can tell us what kinds of user inputs the program can understand). We also understand you will not be using any state of the art technology for doing natural language understanding, and you will only be spending a couple hours at most solving this problem, so we are aware of the limitations of what you (or anybody, given this sort of restrictions) will be able to implement. We are mostly interested in seeing the architecture you come up with, and the quality of your code.

You should write one Java (8) class (just one so that it is easy for you to send us the code and for us to run it). You can use any number of inner classes. You can tell us, if you wish, through comments in the code, which of those inner classes you would normally implement as separate classes (but this is not required). Feel free to document the code as much as you wish (the more the better, but again, this is not required, only for “bonus points”) We understand this is not the “normal” way in which you would write this code, and that’s OK.

Finally, you can use anything available in Java 8, but you can’t use any third party package. All you will send us is the one class source code. We will compile it and run it on our computers, and will test it.

**Solution**

See `entryPoint()` method on [https://github.com/excelsiorsoft/kasisto-interview/blob/master/kasisto-test/src/test/java/com/excelsiorsoft/banking/assistant/BankingAssistantTest.java](https://github.com/excelsiorsoft/kasisto-interview/blob/master/kasisto-test/src/test/java/com/excelsiorsoft/banking/assistant/BankingAssistantTest.java) 

or an equivalent and runnable Java class:

[https://github.com/excelsiorsoft/kasisto-interview/blob/master/kasisto-test/src/main/java/com/excelsiorsoft/banking/assistant/BankingAssistant.java](https://github.com/excelsiorsoft/kasisto-interview/blob/master/kasisto-test/src/main/java/com/excelsiorsoft/banking/assistant/BankingAssistant.java)

that could be invoked via its `main()` method

**References:** 

Some interesting ideas on implementing pattern matching are borrowed from [here](https://kerflyn.wordpress.com/2012/05/09/towards-pattern-matching-in-java/)


