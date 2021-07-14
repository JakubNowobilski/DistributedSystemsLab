const C = require("./constants.js")
const mathjs = require("mathjs")
const axios = require("axios");

function fetchCurrentWeather(lat, lng){
    return Promise.all([
        axios.get(`${C.openWeatherURL}?lat=${lat}&lon=${lng}&appid=${C.openWeatherAPI_KEY}&units=metric`)
            .then(result => result.data)
            .catch(error => undefined),
        axios.get(`${C.weatherApiCurrentURL}?key=${C.weatherApiAPI_KEY}&q=${lat},${lng}`)
            .then(result => result.data?.current)
            .catch(error => undefined),
        axios.get(`${C.climaCellURL}?location=${lat},${lng}&fields=${C.climaCellFields}&timesteps=current&units=metric&apikey=${C.climaCellAPI_KEY}`)
            .then(result => result.data?.data?.timelines[0]?.intervals[0]?.values)
            .catch(error => undefined)
    ]).then(results => {
        let weather = new C.CurrentWeather();
        weather.temperature = retrieveValue([results[0]?.main?.temp, results[1]?.temp_c, results[2]?.temperature], -90, 60)
        weather.temperatureApparent = retrieveValue([results[0]?.main?.feels_like, results[1]?.feelslike_c, results[2]?.temperatureApparent], -100, 80)
        weather.precipitationIntensity = retrieveValue([results[1]?.precip_mm, results[2]?.precipitationIntensity], 0, 26)
        weather.precipitationProbability = retrieveValue([results[2]?.precipitationProbability], 0, 100)
        weather.cloudCover = retrieveValue([results[0]?.clouds?.all, results[1]?.cloud, results[2]?.cloudCover], 0, 100)
        weather.wind = retrieveValue([results[0]?.wind?.speed, results[1]?.wind_kph, results[2]?.windSpeed], 0, 300)
        weather.windDegree = retrieveValue([results[0]?.wind?.deg, results[1]?.wind_degree, results[2]?.windDirection], 0, 360)
        weather.humidity = retrieveValue([results[0]?.main?.humidity, results[1]?.humidity, results[2]?.humidity], 0, 100)
        weather.precipitationType = results[2]?.precipitationType
        weather.conditionText = results[1]?.condition?.text || results[0]?.weather[0]?.description
        weather.conditionIcon = "http://" + results[1]?.condition?.icon?.substring(2) || (results[0]?.weather[0]?.icon ? `http://openweathermap.org/img/wn/${results[0]?.weather[0]?.icon}@2x.png` : undefined)
        return weather
    }).catch(error => {
        console.log(error)
        return {error: error}
    })
}

function retrieveValue(values, min = 0, max = 1){
    values = values.filter(v => v !== undefined && typeof v === "number")
    if(values.length !== 0){
        const mean = mathjs.mean(values)
        values = values.map(v => rescale(v, min, max))
        const std = mathjs.std(values)
        if(std < 0.15){
            return mathjs.round(mean, 1)
        }
    }
    return undefined
}

function rescale(value, min, max){
    return (value - min) / (max - min)
}

module.exports = fetchCurrentWeather
