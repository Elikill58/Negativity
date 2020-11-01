package com.elikill58.negativity.universal.utils;

import java.util.StringJoiner;

public class ChatUtils {


	public static final long YEARS = 3600 * 24 * 30 * 12;
	public static final long MONTHS = 3600 * 24 * 30;
	public static final long WEEKS = 3600 * 24 * 7;
	public static final long DAYS = 3600 * 24;
	public static final long HOURS = 3600;
	public static final long MINUTES = 60;
	public static final long SECONDS = 1;
	
	/**
	 * Get the time to string but rounded at value.
	 * For example, if you ask for 1 month 2 hours (in milliseconds)
	 * You will see "1mo "
	 * 
	 * @param millis the time in milliseconds
	 * @return time rounded at major value
	 */
	public static String getTimeFromLong(long millis) {
		long time = millis / 1000; // set in seconds from milliseconds
		if(time > YEARS) {
			return (((double) time / YEARS)) + "years ";
		}
		if(time > MONTHS) {
			return (((double) time / MONTHS)) + "mo ";
		}
		if(time > WEEKS) {
			return (((double) time / WEEKS)) + "w ";
		}
		if(time > DAYS) {
			return (((double) time / DAYS)) + "d";
		}
		if(time > HOURS) {
			return (((double) time / HOURS)) + "h ";
		}
		if(time > MINUTES) {
			return (((double) time / MINUTES)) + "m ";
		}
		return (((double) time / SECONDS)) + "s";
	}
	
	/**
	 * Get a visual for the current times.
	 * (Don't show milliseconds, only seconds and more)
	 * 
	 * @param millis the time to show in milliseconds
	 * @return a visual string for the given time
	 */
	public static String getFullTimeFromLong(long millis) {
		StringJoiner s = new StringJoiner(" ");
		long time = millis / 1000; // set in seconds from milliseconds
		if(time > YEARS) {
			int years = (int) (time / YEARS);
			s.add(years + "years");
			time -= years * YEARS;
		}
		if(time > MONTHS) {
			int months = (int) (time / MONTHS);
			s.add(months + "mo");
			time -= months * MONTHS;
		}
		if(time > WEEKS) {
			int weeks = (int) (time / WEEKS);
			s.add(weeks + "w");
			time -= weeks * WEEKS;
		}
		if(time > DAYS) {
			int days = (int) (time / DAYS);
			s.add(days + "d");
			time -= days * DAYS;
		}
		if(time > HOURS) {
			int hours = (int) (time / HOURS);
			s.add(hours + "h");
			time -= hours * HOURS;
		}
		if(time > MINUTES) {
			int minutes = (int) (time / MINUTES);
			s.add(minutes + "m");
			time -= minutes * MINUTES;
		}
		if(time > SECONDS) {
			int seconds = (int) (time / SECONDS);
			s.add(seconds + "s");
			time -= seconds * SECONDS;
		}
		return s.toString();
	}

	/**
	 * Get the seconds corresponding to the duration
	 * 
	 * @param duration Time in string
	 * @return the time in seconds of the given duration string
	 */
	public static long parseDurationToSeconds(String duration) {
		long time = 0;
		String stringTime = "", timeKey = "";
		for (String c : duration.split("")) {
			if (UniversalUtils.isInteger(c)) {
				if(!timeKey.isEmpty()) {
					switch (c) {
						case "s":
							time += Integer.parseInt(stringTime) * SECONDS;
							break;
						case "m":
							time += Integer.parseInt(stringTime) * MINUTES;
							break;
						case "h":
							time += Integer.parseInt(stringTime) * HOURS;
							break;
						case "j":
						case "d":
							time += Integer.parseInt(stringTime) * DAYS;
							break;
						case "mu":
						case "mo":
							time += Integer.parseInt(stringTime) * MONTHS;
							break;
						case "y":
						case "yo":
							time += Integer.parseInt(stringTime) * YEARS;
							break;
						default:
							throw new IllegalArgumentException("Unknown time marker '" + c + "'");
					}
					timeKey = "";
					stringTime = "";
				}
				
				stringTime += c;
			} else {
				timeKey += c;
			}
		}

		if (!stringTime.isEmpty()) {
			time += Integer.parseInt(stringTime);
		}

		return time;
	}
}
