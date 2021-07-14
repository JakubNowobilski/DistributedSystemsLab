import {HourData} from './hourData';

export class HourlyWeather{
  constructor(
    public maxTemperature: number | undefined,
    public minTemperature: number | undefined,
    public avgTemperature: number | undefined,
    public conditionText: string | undefined,
    public conditionIcon: string | undefined,
    public date: string | undefined,
    public sunrise: string | undefined,
    public sunset: string | undefined,
    public hours: Array<HourData>
  ) {
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.avgTemperature = avgTemperature;
    this.conditionText = conditionText;
    this.conditionIcon = conditionIcon;
    this.date = date;
    this.sunrise = sunrise;
    this.sunset = sunset;
    this.hours = hours;
  }
}
