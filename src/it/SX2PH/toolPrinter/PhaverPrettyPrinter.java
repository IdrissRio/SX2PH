package it.SX2PH.toolPrinter;

import java.util.ArrayList;

import it.SX2PH.HybridAutomata.Component;
import it.SX2PH.HybridAutomata.HYaut;
import it.SX2PH.HybridAutomata.Location;
import it.SX2PH.HybridAutomata.Parameter;
import it.SX2PH.HybridAutomata.Parameter.Type;
import it.SX2PH.HybridAutomata.Transition;
import it.SX2PH.application.Application;

public class PhaverPrettyPrinter extends PrettyPrinter{


	private void printHyAut(HYaut hy) {
		printSection("Automaton " + hy.getId());
		printLine("automaton " + hy.getAs());
		this.increaseIndentation();
		printControlledVariable(hy);
		printInputVariable(hy);
		printSyncLabel(hy);
		printLocation(hy);
		printInitially(hy);
		printEnd();
		printNewLine();
		printNewLine();
	}

	@Override
	public void printAutomaton(HYaut hy){
		printAutomatonInformation(hy);
		printConstants(hy);
		printHyAut(hy);
	}
	
	private void printEnd() {
		this.decreaseIndentation();
		printLine("end");
	}
	
	private void printInitially(HYaut hy) {
	
		printLine("initially:" + hy.getInitialState()+";");
	}
	
	private void printLocation(HYaut hy) {
		for(Location l: hy.getLocations()) {
			if(l.getFlow()!=null) {
				printLine("loc " + l.getName() +": while " + l.getInvariant() + " wait");
				printLine("{");
				this.increaseIndentation();
				printLine(l.getFlow());
				this.decreaseIndentation();
				printLine("};");
				printNewLine();
				printAssociatedTransitions(l.getIdentifier(),hy);
			}else {
				printLine("loc " + l.getName() +": while " + l.getInvariant() + " wait;");

			}
		}	
	}
	
	private void printAssociatedTransitions(String id, HYaut hy) {
		for(Transition t: hy.getTransitions()) {
			if(!t.getSoruce().equals(id))continue;
			if(t.getAssignment()!=null) {
				printLine("when " + t.getGuard()+ " sync " + t.getLabel() +" do");
				printLine("{");
				this.increaseIndentation();
				printLine(t.getAssignment());
				this.decreaseIndentation();
				printLine("} goto " + t.getTarget() +";");
				printNewLine();
			}
			else {
				printLine("when " + t.getGuard() + " sync " + t.getLabel() +"");
				printLine("goto " + t.getTarget() +";");
				printNewLine();
			}
		}
	}
	
	private void printSyncLabel(HYaut hy) {
		ArrayList<String> labels = new ArrayList<String>();
		for(Component c : hy.getComponents()) {
			for(Parameter p: c.getParameters()) {
				if(p.getType()==Type.LABEL && !labels.contains(p.getName())) {
					labels.add(p.getName());
				}
			}
		}
		printLine("synclabs: " + PrettyPrinter.printFromArrayWithSemicolon(labels));
	}
	
	private void printInputVariable(HYaut hy) {
	ArrayList<String> variables = new ArrayList<String>();
	for(Component c : hy.getComponents()) {
		for(Parameter p: c.getParameters()) {
			if(!p.isControlled() && p.getType()!=Type.LABEL &&
					!p.isConstant() && !variables.contains(p.getName())) {
				variables.add(p.getName());
			}
		}
	}
	if(!variables.isEmpty())
		printLine("input_var: " + PrettyPrinter.printFromArrayWithSemicolon(variables));
}
	
	private void printControlledVariable(HYaut hy) {
		ArrayList<String> cVariables = new ArrayList<String>();
		for(Component c : hy.getComponents()) {
			for(Parameter p: c.getParameters()) {
				if(p.isControlled() && p.getType()!=Type.LABEL &&
						!p.isConstant() && !cVariables.contains(p.getName())) {
					cVariables.add(p.getPrefix()+p.getName());
				}
			}
		}
		if(!cVariables.isEmpty())
			printLine("contr_var: " + PrettyPrinter.printFromArrayWithSemicolon(cVariables));
	}
	
	private void printConstants(HYaut hy){
		Boolean first = true;
		ArrayList<String> constants = new ArrayList<String>();
		for(Component c : hy.getComponents()) {
			for(Parameter p: c.getParameters()) {
				if(p.isConstant() && !constants.contains(p.getName())) {
					if(first)
						printSection("Constants");
					first=false;
					constants.add(p.getName());
					printLine(p.getPrefix()+p.getName() +":=" +p.getValue() +";");
				}
			}
		}
		
		printNewLine();
	}
	
	@Override
	public void printAutomatonInformation(HYaut hy) {
		if(!Application.verbose)return;
		printLine("// Automaton name: " + hy.getId());
		printLine("// Instance name: "+ hy.getAs());
		printLine("// Number of locations: "+ hy.getLocations().size());
		printLine("// Number of transitions: "+ hy.getTransitions().size());
		printLine("// Number of parameter: " +hy.getComponents().get(0).getParameters().size());
	}
}
