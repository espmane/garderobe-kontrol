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

async function loadWeather() {
    try {
        const response = await fetch("/api/weather");

        if (!response.ok) {
            throw new Error("Could not fetch weather");
        }

        const weather = await response.json();
        displayWeather(weather);

    } catch (error) {
        console.error(error);
        weatherContainer.textContent = "Kunne ikke hente dagens vejr.";
    }
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
        activity: getSelectedRadio("activity"),
        dayType: getSelectedRadio("dayType"),
        availableClothes: getSelectedOutfit()
    };
}

function getSelectedOutfit() {
    return {
        headwear: getSelectedValues("headwear"),
        accessories: getSelectedValues("accessories"),
        baseLayer: getSelectedValues("baseLayer"),
        midtLayer: getSelectedValues("midtLayer"),
        jacket: getSelectedValues("jacket"),
        shirt: getSelectedValues("shirt"),
        pants: getSelectedValues("pants"),
        shoes: getSelectedValues("shoes")
    };
}

async function getOutfitRecommendation(outfitData) {
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

function displayRecommendation(result) {
    recommendation.replaceChildren();

    const outfit = result.recommendedOutfit;
    const categories = {
        headwear: "Hovedbeklædning",
        accessories: "Tilbehør",
        baseLayer: "Inderste lag",
        midtLayer: "Mellemlag",
        jacket: "Jakke",
        shirt: "Trøje",
        pants: "Bukser",
        shoes: "Sko"
    };

    const heading = document.createElement("h2");
    heading.textContent = "Anbefalet outfit";

    const list = document.createElement("ul");

    for (const key in categories) {
        const item = document.createElement("li");
        if (outfit[key].length === 0) {
            item.textContent = `${categories[key]}: ingenting`;
        } else {
            item.textContent = `${categories[key]}: ${outfit[key]}`;
        }
        list.appendChild(item);
    }

    const explanation = document.createElement("p");
    explanation.textContent = result.explanation;

    recommendation.append(heading, explanation, list);
    resultSection.hidden = false;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const outfitData = buildOutfitRequest();

    if (!outfitData.activity || !outfitData.dayType) {
        resultSection.hidden = false;
        recommendation.textContent =
            "Vælg aktivitetsniveau og om barnet skal være inde eller ude.";
        return;
    }

    submitButton.disabled = true;
    submitButton.textContent = "Finder passende tøj...";

    resultSection.hidden = false;
    recommendation.textContent =
        "Vi finder den bedste påklædning til dit barn...";

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
