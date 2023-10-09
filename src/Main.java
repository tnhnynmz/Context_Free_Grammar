import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	static CYK cyk;
	static String[][] cykTable;
	static String globalVariable;
	static ArrayList<String> derivationSteps=new ArrayList<String>();
	public static void main(String[] args) throws FileNotFoundException {

		
		
		ArrayList<String> variables=new ArrayList<String>(); 
		HashMap<String,ArrayList<String>> rules = new HashMap<String,ArrayList<String>>();
		String fileName="CFG.txt";
		
		readCFG(variables,rules,fileName);
		//addS0(variables,rules);
		for(int i=0;i<variables.size();i++) {
			eliminateEmpty(variables,rules);
		}
		System.out.println("Empty String Productions Eliminated:");
		printMap(variables, rules);
		for(int i=0;i<variables.size();i++) {
			eliminateUnit(variables, rules);
		}
		System.out.println("Unit Productions Eliminated:");
		printMap(variables, rules);
		System.out.println("Terminals are isolated:");
		isolateTerminals(variables, rules);
		System.out.println("Too Long Variable Productions Eliminated:");
		isolateMultipleProduction(variables, rules);
		
		String cnfFile="CNF.txt";
		printCNFtoFile(variables, rules,cnfFile);
		
		cyk=new CYK();
		cyk.CYK(cnfFile);
		
		cykTable=cyk.callCykTable();
		boolean stringIsDerivable=false;
		if(cykTable[cykTable.length-1][0].contains(variables.get(0))) {
			stringIsDerivable=true;
		}
		
		
		if(stringIsDerivable==true) {
			globalVariable=variables.get(0);
		
		
		
			derivationSteps.add(variables.get(0));
		
		
			deriveCell(cykTable.length-1, 0, variables.get(0), variables, rules);
		
		
			System.out.println("\nDERIVATION STEPS:");
			for(int i=0;i<derivationSteps.size();i++) {
				if(i<derivationSteps.size()-1) {
					System.out.print(derivationSteps.get(i)+"=>");
				}
				else {
					System.out.print(derivationSteps.get(i));
				}
			}
		
			System.out.println();
			Node rootNode=new Node(cykTable.length-1, 0,variables.get(0));
			buildTree(rootNode, variables, rules);
			System.out.println("\nPARSE TREE:");
			printBinaryTree(rootNode);
		}
		else {
			System.out.println("STRING IS NOT DERIVABLE");
		}
		
		
	}
	
	public static void printBinaryTree(Node root) {
        LinkedList<Node> treeLevel = new LinkedList<Node>();
        treeLevel.add(root);
        LinkedList<Node> temp = new LinkedList<Node>();
        int counter = 0;
        int height = heightOfTree(root)-1;
        //System.out.println(height);
        double numberOfElements = (Math.pow(2 , (height + 1)) - 1);
        //System.out.println(numberOfElements);
        while (counter <= height) {
            Node removed = treeLevel.removeFirst();
            if (temp.isEmpty()) {
                printSpace(numberOfElements / Math.pow(2 , counter + 1), removed);
            } else {
                printSpace(numberOfElements / Math.pow(2 , counter), removed);
            }
            if (removed == null) {
                temp.add(null);
                temp.add(null);
            } else {
                temp.add(removed.getLeftChild());
                temp.add(removed.getRightChild());
            }
 
            if (treeLevel.isEmpty()) {
                System.out.println("");
                System.out.println("");
                treeLevel = temp;
                temp = new LinkedList<>();
                counter++;
            }
 
        }
    }
 
	public static void printSpace(double n, Node removed){
		for(;n>0;n--) {
			System.out.print(" ");
		}
		if(removed == null){
			System.out.print("");
		}
		else {
			System.out.print(removed.getVariable());
		}
	}
 
	public static int heightOfTree(Node root){
		if(root==null){
			return 0;
		}
		return 1+ Math.max(heightOfTree(root.getLeftChild()),heightOfTree(root.getRightChild()));
	}
	
	
	 static void readCFG(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules, String fileName) throws FileNotFoundException {
		File file=new File(fileName);
		Scanner scanner=new Scanner(file);
		
		while(scanner.hasNextLine()) {
			String line=scanner.nextLine();			//pair[0] key, 
			String[] pair= line.split(">");
			variables.add(pair[0]);
			ArrayList<String> tempArrayList=new ArrayList<String>();
			String tempArray[]=pair[1].split("\\|");
			for (int i=0;i<tempArray.length;i++) {
				tempArrayList.add(tempArray[i]);
			}
			rules.put(pair[0],tempArrayList);
		}
		
		scanner.close();
		System.out.println("Given Grammer is read:");
		printMap(variables, rules);
	}
	
	 static void addS0(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		variables.add(0, "S0");
		ArrayList<String> tempArrayList=new ArrayList<String>();
		tempArrayList.add(variables.get(1));
		rules.put("S0", tempArrayList);
		System.out.println("Step 1 Completed, new start variable added\n");
		for (String string : variables) {
			System.out.println(string +">" +rules.get(string).toString());
		}
	}

	 
	 
	 private static void deriveCell(int x, int y, String variable,ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		 
		 if(x==1) {
			 
			 for(int k=0; k<globalVariable.length();k++){
					StringBuilder sb=new StringBuilder(globalVariable);
					if(globalVariable.substring(k,k+1).equals(variable)){
						sb.replace(k,k+1,cykTable[0][y]);
						globalVariable=sb.toString();
						String tempString=globalVariable;
						derivationSteps.add(tempString);
						return ;
					}
				}
		 }
		 
		 for(int i=1;i<x;i++){							//for change cells vertically ------for change cells diagonally
				String[] combinations=  cyk.getAllCombinations(cykTable[i][y].split("\\s"), cykTable[x-i][y+i].split("\\s"));     
				for(int j=0;j<combinations.length;j++){
					if(combinations[j].length()>1 && rules.get(variable).contains(combinations[j])){
						for(int k=0; k<globalVariable.length();k++){
							StringBuilder sb=new StringBuilder(globalVariable);
							if(globalVariable.substring(k,k+1).equals(variable)){
								sb.replace(k,k+1,combinations[j]);
								globalVariable=sb.toString();
								String tempString=globalVariable;//globalVariable is a global static string, so every recursion can recognize changes
								derivationSteps.add(tempString);//derivationSteps is a global static ArrayList<String> so every recursion recognize changes
								deriveCell(i,y,combinations[j].substring(0,1),variables,rules);
								deriveCell(x-i,y+i,combinations[j].substring(1,2),variables,rules);
								return ;
							}
						}
					}
				}	
			}
	 }
	 
	 
	 private static void buildTree(Node parent, ArrayList<String> variables,HashMap<String, ArrayList<String>> rules){
	 
			if(parent.getxCoordinate()==1){
				Node child= new Node(0,parent.getyCoordinate(),cykTable[0][parent.getyCoordinate()]);
				parent.setLeftChild(child);
				return;
			}

			for(int i=1;i<parent.getxCoordinate();i++){			//for change cells vertically --------------------------------------for change cells diagonally
				String[] combinations= cyk.getAllCombinations(cykTable[i][parent.getyCoordinate()].split("\\s"), cykTable[parent.getxCoordinate()-i][parent.getyCoordinate()+i].split("\\s"));
				for(int j=0;j<combinations.length;j++){
					if(combinations[j].length()>1 && rules.get(parent.getVariable()).contains(combinations[j])){
						Node leftChild=new Node(i,parent.getyCoordinate(),combinations[j].substring(0,1));
						Node rightChild=new Node(parent.getxCoordinate()-i,parent.getyCoordinate()+i,combinations[j].substring(1,2));
						parent.setLeftChild(leftChild);
						parent.setRightChild(rightChild);
						buildTree(leftChild,variables,rules);
						buildTree(rightChild,variables,rules);
						return;
					}
				}
			}


		}
	 
	 
	 private static void isolateMultipleProduction(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		 int asciiCount=65;
		 for(int i=0; i<variables.size(); i++){
		 	String currentVariable=variables.get(i);
		 	for(int j=0; j<rules.get(currentVariable).size();j++){
		 		String currentProduction=rules.get(currentVariable).get(j);
		 		while(currentProduction.length()>2){
		 			String firstTwoCharacters= currentProduction.substring(0,2);
		 			String str="";
		 			for(int k=0; k<variables.size();k++ ){
		 				str=new Character((char) asciiCount).toString();
		 				if(variables.contains(str)){
		 					asciiCount++;
		 				}
		 				else{
		 					break;
		 				}
		 			}
		 			variables.add(str);

		 			ArrayList<String> tempArrayList= new ArrayList<String>();
		 		 	tempArrayList.add(firstTwoCharacters);
		 		 	rules.put(str,tempArrayList);

		 			for(int y=0; y<variables.size();y++){
		 				String scannedVariable=variables.get(y);
		 				if(rules.get(scannedVariable).size()>1){
		 					for(int z=0;z<rules.get(scannedVariable).size();z++){
		 						String scannedProduction=rules.get(scannedVariable).get(z);
		 						scannedProduction= scannedProduction.replace(firstTwoCharacters,str);
		 						rules.get(scannedVariable).set(z,scannedProduction);
		 						currentProduction=scannedProduction;
		 					}
		 				}
		 			}
		 		}
		 	}
		 }
		 
		 printMap(variables, rules);
	 }
	 
	 private static void isolateTerminals(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		 int asciiCount=65;
		 for(int i=0; i<variables.size();i++){
		 	String currentVariable=variables.get(i);
		 	for(int j=0; j< rules.get(currentVariable).size();j++){
		 		String currentProduction= rules.get(currentVariable).get(j);
		 		for(int k=0; k<currentProduction.length();k++){
		 			String charOfProduction=currentProduction.substring(k,k+1);
		 			if(!variables.contains(charOfProduction) && currentProduction.length()>1){
		 				boolean previouslyCreated=false;
		 				String keyToBeReplaced="";
		 				for(int w=0; w<variables.size();w++){
		 					if(rules.get(variables.get(w)).size()==1 && rules.get(variables.get(w)).get(0).equals(charOfProduction)){
		 						previouslyCreated=true;
		 						keyToBeReplaced=variables.get(w);
		 					}
		 				}
		 				//if key for that terminal previously created
		 				if(previouslyCreated==true){
		 					StringBuilder tempSb= new StringBuilder(currentProduction);
		 					tempSb.replace(k,k+1,keyToBeReplaced);
		 					rules.get(currentVariable).set(j,tempSb.toString());;
		 					currentProduction=tempSb.toString();
		 				}
		 				// if key for that terminal needs to be created
		 				else{
		 					String str="";
		 					for(int y=0; y<variables.size();y++){
		 						str = new Character((char) asciiCount).toString();
		 						if(variables.contains(str)){
		 							asciiCount++;
		 						}
		 						else{
		 							break;
		 						}
		 					}

		 					
		 					variables.add(str);
		 					ArrayList<String> tempArrayList= new ArrayList<String>();
		 					tempArrayList.add(charOfProduction);
		 					
		 					rules.put(str,tempArrayList);
		 					StringBuilder tempSb= new StringBuilder(currentProduction);
		 					tempSb.replace(k,k+1,str);
		 					rules.get(currentVariable).set(j,tempSb.toString());
		 					currentProduction=tempSb.toString();
		 				}		
		 			}
		 		}	
		 	}
		 }
		 printMap(variables, rules);
		 
	 }
	 
	 private static void eliminateUnit(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		 
		 for(int i=0; i<variables.size();i++){
				for(int j=0;j<rules.get(variables.get(i)).size();j++){
					if(rules.get(variables.get(i)).get(j).length()==1 && variables.contains(rules.get(variables.get(i)).get(j))){
						for(int k=0; k<rules.get(rules.get(variables.get(i)).get(j)).size();k++){
							if(!rules.get(variables.get(i)).contains(rules.get(rules.get(variables.get(i)).get(j)).get(k))){
								rules.get(variables.get(i)).add(rules.get(rules.get(variables.get(i)).get(j)).get(k));
							}
						}
						rules.get(variables.get(i)).remove(j);
					}
				}



			}
		// printMap(variables, rules);
		 
	 }
	 
	 private static void eliminateEmpty(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		 
		 for(int i=1; i<variables.size(); i++){
				String epsilonKey="";
				if(rules.get(variables.get(i)).contains("#")){
					epsilonKey=variables.get(i);
					for(int j=0; j<rules.get(variables.get(i)).size(); j++){
						if(rules.get(variables.get(i)).get(j).equals("#")){
							rules.get(variables.get(i)).remove(j);
						}
					}
					
					for(int j=0; j<variables.size();j++){
						for(int k=0;k<rules.get(variables.get(j)).size();k++){
							String readOperation=rules.get(variables.get(j)).get(k);
							if(rules.get(variables.get(j)).get(k).contains(epsilonKey)){
								for(int z=0; z< readOperation.length();z++){
									String tempOperation=readOperation;
									if(tempOperation.charAt(z)== epsilonKey.charAt(0)){
										StringBuilder sb= new StringBuilder(tempOperation);
										sb.deleteCharAt(z);
										tempOperation=sb.toString();
										if(!rules.get(variables.get(j)).contains(tempOperation)){
											rules.get(variables.get(j)).add(tempOperation);
										}
									}
								}	
							}
						}
					}
				}


				

			}
		 
		 
		// printMap(variables, rules);
	 }
	 
	 
	
	private static void printMap(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {

        
        for (String s: variables ) {
            System.out.println(s + " -> " + rules.get(s));
        }

        System.out.println(" ");
    }
	
	private static void printCNFtoFile(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules, String cnfFile) {
		try {
			FileWriter fileWriter=new FileWriter(cnfFile);
			fileWriter.write(variables.get(0)+"\n");
			
			
			//print terminals to file
			String terminals=terminalToString(variables, rules);
			fileWriter.write(terminals);
			
			
			//print variables to file
			String variablesString=variablesToString(variables);
			fileWriter.write(variablesString);
			//print rules to file
			String rulesString=rulesToString(variables, rules);
			fileWriter.write(rulesString);
			
			
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		


		
		
		
		
	}
	
	
	
	private static String rulesToString(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		String rulesString="";
		
		for(int i=0;i<variables.size();i++) {
			String currentVariable=variables.get(i);
			rulesString += currentVariable + " ";
			for(int j=0; j<rules.get(currentVariable).size();j++) {
				String currentProductionString=rules.get(currentVariable).get(j);
				rulesString+=currentProductionString +" ";
			}
			
			StringBuilder sBuilder=new StringBuilder(rulesString);
			sBuilder.replace(rulesString.length()-1, rulesString.length(),"\n");
			rulesString=sBuilder.toString();
		}
		
		
		
		return rulesString;
	}
	
	private static String variablesToString(ArrayList<String> variables) {
		
		String variablesString="";
		for(int i=0; i<variables.size();i++) {
			variablesString+= variables.get(i)+" ";
		}
		StringBuilder sBuilder=new StringBuilder(variablesString);
		sBuilder.replace(variablesString.length()-1, variablesString.length(),"\n");
		variablesString=sBuilder.toString();
		return variablesString;
	}
	
	
	private static String terminalToString(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		String terminals="";
		//findterminals
		ArrayList<String> terminalsList=findTerminals(variables, rules);
		for(int i=0;i<terminalsList.size();i++) {
			terminals+=terminalsList.get(i)+" ";
		}
		StringBuilder sBuilder=new StringBuilder(terminals);
		sBuilder.replace(terminals.length()-1, terminals.length(),"\n");
		terminals=sBuilder.toString();
		return terminals;
	}
	
	private static ArrayList<String> findTerminals(ArrayList<String> variables, HashMap<String, ArrayList<String>> rules) {
		
		ArrayList<String> terminals=new ArrayList<String>();

		for(int i=0; i<variables.size();i++){
			String currentVariable=variables.get(i);
			for(int j=0;j<rules.get(currentVariable).size();j++){
				String currentProduction=rules.get(currentVariable).get(j);
				for(int k=0;k<currentProduction.length();k++){
					String charOfProduction=currentProduction.substring(k,k+1);
					if(!(variables.contains(charOfProduction)) && !(terminals.contains(charOfProduction))){
						terminals.add(charOfProduction);
					}
				}
			}
		}
		
		return terminals;
	}
	
}
	
	


