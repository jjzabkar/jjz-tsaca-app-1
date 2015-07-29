package com.jjz.tsaca.domain;

import java.util.Date;

import fr.dudie.onebusaway.model.ArrivalAndDeparture;

public class ArrivalDeparture extends ArrivalAndDeparture {

	private SortableColor color;
	private Date myEstimatedBoardableTime;

	public SortableColor getColor() {
		return color;
	}

	public void setColor(SortableColor color) {
		this.color = color;
	}

	public Date getMyEstimatedBoardableTime() {
		return myEstimatedBoardableTime;
	}

	public void setMyEstimatedBoardableTime(Date myEstimatedBoardableTime) {
		this.myEstimatedBoardableTime = myEstimatedBoardableTime;
	}

}
