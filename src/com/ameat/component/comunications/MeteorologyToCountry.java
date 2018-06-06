package com.ameat.component.comunications;

import com.ameat.component.meteorology.Location;

public interface MeteorologyToCountry extends ComunicationInterface{
	public Location getMeteInfo(String locationStr);
}
