import {HourlyWeather} from './hourlyWeather';

export class HourlyWeatherResponse{
  constructor(
    public providedLocation: string | undefined,
    public providedDate: string | undefined,
    public date: string | undefined,
    public location: any | undefined,
    public hourlyWeather: HourlyWeather | undefined
  ) {
    this.providedLocation = providedLocation;
    this.providedDate = providedDate;
    this.date = date;
    this.location = location;
    this.hourlyWeather = hourlyWeather;
  }
}
