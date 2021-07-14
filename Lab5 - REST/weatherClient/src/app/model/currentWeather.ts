export class CurrentWeather{
  constructor(
    public temperature: number | undefined,
    public temperatureApparent: number | undefined,
    public precipitationIntensity: number | undefined,
    public precipitationProbability: number | undefined,
    public cloudCover: number | undefined,
    public wind: number | undefined,
    public windDegree: number | undefined,
    public humidity: number | undefined,
    public precipitationType: number | undefined,
    public conditionText: string | undefined,
    public conditionIcon: string | undefined,
  ) {
    this.temperature = temperature;
    this.temperatureApparent = temperatureApparent;
    this.precipitationIntensity = precipitationIntensity;
    this.precipitationProbability = precipitationProbability;
    this.cloudCover = cloudCover;
    this.wind = wind;
    this.windDegree = windDegree;
    this.humidity = humidity;
    this.precipitationType = precipitationType;
    this.conditionText = conditionText;
    this.conditionIcon = conditionIcon;
  }
}
