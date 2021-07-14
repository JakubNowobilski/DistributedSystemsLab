export class HourData{
  constructor(
    public time: string | undefined,
    public temperature: number | undefined,
    public conditionText: string | undefined,
    public conditionIcon: string | undefined,
    public precipitationIntensity: number | undefined
  ) {
    this.time = time;
    this.temperature = temperature;
    this.conditionText = conditionText;
    this.conditionIcon = conditionIcon;
    this.precipitationIntensity = precipitationIntensity;
  }
}
