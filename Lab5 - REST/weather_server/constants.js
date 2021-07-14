const K = require("API_keys")

class CurrentWeather{
    constructor() {
        this.temperature = undefined;
        this.temperatureApparent = undefined;
        this.precipitationIntensity = undefined;
        this.precipitationProbability = undefined;
        this.cloudCover = undefined;
        this.wind = undefined;
        this.windDegree = undefined;
        this.humidity = undefined;
        this.precipitationType = undefined;
        this.conditionText = undefined;
        this.conditionIcon = undefined;
    }
}

class CurrentWeatherResponse{
    constructor() {
        this.providedLocation = undefined;
        this.date = undefined;
        this.location = undefined;
        this.currentWeather = undefined;
    }
}

class HourlyWeather{
    constructor() {
        this.maxTemperature = undefined;
        this.minTemperature = undefined;
        this.avgTemperature = undefined;
        this.conditionText = undefined;
        this.conditionIcon = undefined;
        this.date = undefined;
        this.sunrise = undefined;
        this.sunset = undefined;
        this.hours = undefined
    }
}

class HourlyWeatherResponse{
    constructor() {
        this.providedLocation = undefined;
        this.providedDate = undefined;
        this.date = undefined;
        this.location = undefined;
        this.hourlyWeather = undefined;
    }
}

class HourData{
    constructor() {
        this.time = undefined;
        this.temperature = undefined;
        this.conditionText = undefined;
        this.conditionIcon = undefined;
        this.precipitationIntensity = undefined;
    }
}

module.exports = {
    mapQuestAPI_KEY: K.mapQuestKey,
    mapQuestURL: "http://open.mapquestapi.com/geocoding/v1/address",
    mapQuestReverseURL: "http://open.mapquestapi.com/geocoding/v1/reverse",

    openWeatherAPI_KEY: K.openWeatherKey,
    openWeatherURL: "http://api.openweathermap.org/data/2.5/weather",

    weatherApiAPI_KEY: K.weatherApiKey,
    weatherApiCurrentURL: "http://api.weatherapi.com/v1/current.json",
    weatherApiForecastURL: "http://api.weatherapi.com/v1/forecast.json",
    weatherApiHistoryURL: "http://api.weatherapi.com/v1/history.json",

    climaCellAPI_KEY: K.climaCellKey,
    climaCellURL: "https://api.tomorrow.io/v4/timelines",
    climaCellFields: "temperature," +
        "temperatureApparent," +
        "pressureSurfaceLevel," +
        "precipitationIntensity," +
        "precipitationProbability," +
        "precipitationType," +
        "cloudCover," +
        "windSpeed," +
        "windDirection," +
        "humidity",

    CurrentWeather: CurrentWeather,
    CurrentWeatherResponse: CurrentWeatherResponse,
    HourlyWeather: HourlyWeather,
    HourlyWeatherResponse: HourlyWeatherResponse,
    HourData: HourData
}

