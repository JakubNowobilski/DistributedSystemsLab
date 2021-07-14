import {CurrentWeather} from './currentWeather';

export class CurrentWeatherResponse{
  constructor(
    public providedLocation: string | undefined,
    public date: Date | undefined,
    public location: any | undefined,
    public currentWeather: CurrentWeather | undefined
  ) {
    this.providedLocation = providedLocation;
    this.date = date;
    this.location = location;
    this.currentWeather = currentWeather;
  }
}

