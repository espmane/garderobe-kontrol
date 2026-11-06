const form = document.querySelector("#outfitForm");
const weatherContainer = document.querySelector("#weather");
const resultSection = document.querySelector("#result");
const recommendation = document.querySelector("#recommendation");
const submitButton = document.querySelector("#submitButton");

const socket = new WebSocket("ws://localhost:8080/ws")

socket.onmessage = () => throwTomato();

function throwTomato() {
    const tomato = document.getElementById("tomato-animation");
    const splat = document.getElementById("tomato-splat");

    tomato.style.display = splat.style.display = "block";
    splat.style.opacity = 0;

    tomato.animate([
        {opacity: 0, transform: "translate(0, 0) scale(.025) rotate(0deg)"},
        {opacity: 1, transform: "translate(-18vw, -12vh) scale(.22) rotate(360deg)", offset: .22},
        {transform: "translate(-35vw, -30vh) scale(2) rotate(950deg)", offset: .58},
        {opacity: 1, transform: "translate(-50vw, -50vh) scale(13) rotate(1620deg)"}
    ], {
        duration: 1350,
        easing: "linear",
        fill: "forwards"
    }).onfinish = () => {
        tomato.style.display = "none";
        splat.style.opacity = 1;

        setTimeout(() => {
            splat.style.opacity = 0;
            setTimeout(() => splat.style.display = "none", 400);
        }, 150);
    };
}
let userLatitude;
let userLongitude;

async function loadWeather() {
    navigator.geolocation.getCurrentPosition(async (position) => {
        try {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;

            userLatitude = latitude;
            userLongitude = longitude;

            const response = await fetch(
                `/api/weather?latitude=${latitude}&longitude=${longitude}`
            );

            if (!response.ok) {
                throw new Error("Could not fetch weather");
            }

            const weather = await response.json();
            displayWeather(weather);

        } catch (error) {
            console.error(error);
            weatherContainer.textContent = "Kunne ikke hente dagens vejr.";
        }
    }, (error) => {
        console.error(error);
        weatherContainer.textContent = "Kunne ikke finde din lokation.";
    });
}

function displayWeather(weather) {
    weatherContainer.replaceChildren();

    const weatherInfo = document.createElement("div");
    weatherInfo.className = "weather-info";

    const details = document.createElement("div");

    const temperature = document.createElement("p");
    temperature.className = "temperature";
    temperature.textContent = `${weather.hourly.temperature[0]}°C`;

    const description = document.createElement("p");
    description.textContent = weather.description ?? "Dagens vejr";

    const wind = document.createElement("p");
    wind.textContent = `Vind: ${weather.hourly.windSpeed[0]} m/s`;

    details.append(temperature, description, wind);
    weatherInfo.append(details);
    weatherContainer.append(weatherInfo);
}

function getSelectedValues(name) {
    const checkboxes = document.querySelectorAll(
        `input[name="${name}"]:checked`
    );

    return Array.from(checkboxes).map(
        checkbox => checkbox.value
    );
}

function getSelectedRadio(name) {
    const selected = document.querySelector(
        `input[name="${name}"]:checked`
    );

    return selected ? selected.value : null;
}

function buildOutfitRequest() {
    return {
        latitude: userLatitude,
        longitude: userLongitude,
        mobilityLevel: getSelectedRadio("mobilityLevel"),
        outdoorTime: getSelectedRadio("outdoorTime"),
        dailyActivities: getSelectedValues("dailyActivities"),
        availableClothes: getSelectedOutfit()
    };
}

function getSelectedOutfit() {
    return {
        headwear: getSelectedValues("headwear"),
        accessories: getSelectedValues("accessories"),
        baseLayer: getSelectedValues("baseLayer"),
        midLayer: getSelectedValues("midLayer"),
        outerLayer: getSelectedValues("outerLayer"),
        pants: getSelectedValues("pants"),
        shoes: getSelectedValues("shoes")
    };
}

async function getOutfitRecommendation(outfitData) {
    console.log("JSON til backend:", JSON.stringify(outfitData, null, 2));

    const response = await fetch("/api/outfit", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(outfitData)
    });

    if (!response.ok) {
        throw new Error("Could not get outfit recommendation");
    }

    return await response.json();
}

function formatEnum(value) {
    const translations = {
        CAP: "Kasket",
        BEANIE: "Hue",
        BUCKET_HAT: "Bøllehat",
        SUN_HAT: "Solhat",
        BALACLAVA: "Elefanthue",
        HEADBAND: "Pandebånd",

        NECK_WARMER: "Halsedisse",
        MITTENS: "Luffer",
        GLOVES: "Handsker",
        SUN_PROTECTION: "Solbeskyttelse",

        BODY: "Body",
        UNDERWEAR: "Undertøj",
        TIGHTS: "Strømpebukser/Leggings",
        SOCKS: "Strømper",
        WOOL_BASE_LAYER: "Uldundertøj",

        T_SHIRT: "T-shirt",
        LONG_SLEEVE_SHIRT: "Langærmet t-shirt",
        SWEATER: "Sweater",
        FLEECE: "Fleece",
        THERMAL_SHIRT: "Termotrøje",

        RAIN_JACKET: "Regnjakke",
        WINTER_JACKET: "Vinterjakke",
        LIGHT_JACKET: "Let jakke",
        THERMAL_JACKET: "Termojakke",
        SOFTSHELL_JACKET: "Softshelljakke",
        RAIN_PANTS: "Regnbukser",
        THERMAL_PANTS: "Termobukser",
        SNOWSUIT: "Flyverdragt",

        SHORTS: "Shorts",
        REGULAR_PANTS: "Almindelige bukser",
        LEGGINGS: "Leggings",

        SNEAKERS: "Sneakers",
        RAIN_BOOTS: "Gummistøvler",
        WINTER_BOOTS: "Vinterstøvler",
        SANDALS: "Sandaler",
        THERMAL_BOOTS: "Termostøvler",
        INDOOR_SHOES: "Indesko"
    };

    return translations[value] ?? value;
}


function displayRecommendation(result) {
    recommendation.replaceChildren();

    const categories = {
        headwear: "Hovedbeklædning",
        accessories: "Tilbehør",
        baseLayer: "Inderste lag",
        midLayer: "Mellemlag",
        outerLayer: "Overtøj",
        pants: "Bukser",
        shoes: "Fodtøj"
    };

    function displayOutfit(outfit, title) {
        const items = [];

        for (const key in categories) {
            const categoryItems = outfit?.[key] ?? [];

            if (categoryItems.length > 0) {
                items.push({
                    category: categories[key],
                    clothes: categoryItems.map(formatEnum).join(", ")
                });
            }
        }

        if (items.length === 0) {
            return false;
        }

        const heading = document.createElement("h3");
        heading.textContent = title;

        const list = document.createElement("ul");

        items.forEach(item => {
            const listItem = document.createElement("li");
            listItem.textContent = `${item.category}: ${item.clothes}`;
            list.appendChild(listItem);
        });

        recommendation.append(heading, list);

        return true;
    }

        const hasMissingClothes = displayOutfit(
            result.recommendedOutfit, "Tøj, der ikke er registreret i gardaroben"
    );
        if (!hasMissingClothes) {
            const message = document.createElement("p");
            message.textContent = "Den registrerede gardarobe ser ud til at dække barnets behov for denne dag.";
            recommendation.appendChild(message);

        }

        const hasExtraClothes = displayOutfit(
            result.extraClothesToPack,
            "Ekstra skiftetøj at medbringe"
        );

        if (!hasExtraClothes) {
            const message = document.createElement("p");
            message.textContent = "Der vurderes ikke at være behov for ekstra skiftetøj.";
            recommendation.appendChild(message);
        }

        const explanation = document.createElement("p");
        explanation.textContent = result.explanation ?? "";
        recommendation.appendChild(explanation);

        resultSection.hidden = false;
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const outfitData = buildOutfitRequest();

        if (userLatitude == null || userLongitude == null) {
            recommendation.textContent = "Vi kunne ikke finde din lokation. Prøv igen.";
            resultSection.hidden = false;
            return;
        }

        if (!outfitData.mobilityLevel || !outfitData.outdoorTime) {
            resultSection.hidden = false;
            recommendation.textContent =
                "Vælg barnets motoriske niveau og tid indendørs/udendørs.";
            return;
        }

        submitButton.disabled = true;
        submitButton.textContent = "Finder passende tøj...";

        resultSection.hidden = false;
        recommendation.textContent =
            "Vi finder de beklædningsdele, der er relevante for dit barns dag...";

        try {
            const result = await getOutfitRecommendation(outfitData);
            displayRecommendation(result);

        } catch (error) {
            console.error(error);
            recommendation.textContent =
                "Der opstod en fejl. Kunne ikke hente tøjanbefalingen.";

        } finally {
            submitButton.disabled = false;
            submitButton.textContent = "Find passende tøj";
        }
    });

    loadWeather();
