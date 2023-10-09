# Context_Free_Grammar
Solve the parsing problem that check whether a given string belongs to the language given by any Context Free Grammar G1.


Goal was to build a program that checks whether a given string
can be derived from a given context free grammar or not. And if that string was derivable, it
was necessary to show derivation steps and parse tree as a proof.

=>Read CFG.txt file
=>Convert CFG to CNF:
  ->Eliminate empty strings
  ->Eliminate units
  ->Isolate terminals
  ->Isolate Multiple Productions
=>Print new Chomsky Normal Form (CNF) to a new text file
=>Read CNF.txt (take Chomsky Normal Form)
=>Take string input to check from user
=>Parse grammar and initialize terminals, non-terminals and rules
=>Create a new CYK table
=>Fill the CYK table with combinations
=>Compare the string input with CYK table
=>Decide if the string can be derivated from CYK table or not
=>If string can be derivated print Derivation and Tree
  ->Else print string can not be derivated.
