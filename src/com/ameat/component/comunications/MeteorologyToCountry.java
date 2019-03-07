package com.ameat.component.comunications;

import com.ameat.component.meteorology.Location;

public interface MeteorologyToCountry extends ComunicationInterface{
	Location getMeteInfo(String locationStr);
}
