package com.ameat.component.interfaces;

import java.util.Map;

import com.ameat.component.comunications.ComunicationInterface;
import com.ameat.simulation.TimeController;

public interface CompInterface extends ComunicationInterface{
	
	public void init(TimeController timeController, Map<String, ComunicationInterface> comunications);
	
	public void compute();
	
	public void anchorCompute();
	
	public void finished();
}
