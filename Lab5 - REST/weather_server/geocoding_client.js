const C = require("./constants.js")
const axios = require("axios");

function fetchCoords(location){
    const re = new RegExp("^-?\\d+(\\.\\d*)?,-?\\d+(\\.\\d*)?$");
    if(re.test(location)){
        return axios.get(encodeURI(`${C.mapQuestReverseURL}/?key=${C.mapQuestAPI_KEY}&location=${location}`))
            .then(result => {
                return result.data.results[0]?.locations[0]
            })
            .catch(error => {
                return {error: error}
            });
    }else{
        return axios.get(encodeURI(`${C.mapQuestURL}/?key=${C.mapQuestAPI_KEY}&location=${location}`))
            .then(result => {
                return result.data.results[0]?.locations[0]
            })
            .catch(error => {
                return {error: error}
            });
    }
}

function getQuery(location){
    return `${C.mapQuestURL}/?key=${C.mapQuestAPI_KEY}&location=${location}`
}

module.exports = fetchCoords
