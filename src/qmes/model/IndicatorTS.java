package qmes.model;

public class IndicatorTS extends HuskyObject {

	private String name;
	private String surge;
	private String trend;
	private String timespan;

	private double surgeValue;


	public double getSurgeValue() {
		return surgeValue;
	}

	public void setSurgeValue(double surgeValue) {
		this.surgeValue = surgeValue;
	}

	public IndicatorTS(String name, String trend, String surge, String timespan) {
		this.name = name;
		this.surge = surge;
		this.trend = trend;
		this.timespan = timespan;
	}

	public IndicatorTS() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurge() {
		return surge;
	}

	public void setSurge(String surge) {
		this.surge = surge;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	public String getTimespan() {
		return timespan;
	}

	public void setTimespan(String timespan) {
		this.timespan = timespan;
	}

	public String toString() {
		Object[][] kvarray = { { "classname", getClass().getSimpleName() }, { "name", name }, { "surge", surge },
				{ "trend", trend }, { "timespan", timespan }, { "surgeValue", surgeValue }, };
		return kvs(kvarray);
	}
}