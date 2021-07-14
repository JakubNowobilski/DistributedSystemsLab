import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {
  private currentWeatherURL = 'http://localhost:8080/current_weather';
  private hourlyWeatherURL = 'http://localhost:8080/hourly_weather';
  private httpClient: HttpClient;
  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  public fetchCurrentWeather(location: string): Observable<any>{
    return this.httpClient.get(`${this.currentWeatherURL}?location=${location}`);
  }

  public fetchHourlyWeather(location: string, date: string): Observable<any>{
    return this.httpClient.get(`${this.hourlyWeatherURL}?location=${location}&date=${date}`);
  }
}
