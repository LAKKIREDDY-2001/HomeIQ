/**
 * HomeIQ Alexa Custom Skill Handler
 * Cross-device voice bridge connecting Alexa & Echo devices with HomeIQ on Fire TV.
 * Powered by Amazon Bedrock (Claude 3 Haiku) & Fire TV Deep Linking.
 */

const Alexa = require('ask-sdk-core');

// Deep Link URI Schemas registered in HomeIQ AndroidManifest.xml
const DEEP_LINKS = {
    LAUNCH: 'homeiq://dashboard',
    ENERGY_REPORT: 'homeiq://energy_report',
    ECO_MODE: 'homeiq://ecomode',
    FAMILY_GUARDIAN: 'homeiq://family_guardian',
    STOVE_SHUTOFF: 'homeiq://shutoff_stove'
};

/**
 * Dispatches Fire TV intent command via Device Command Gateway / FCM
 */
function sendFireTvDeepLink(uri, actionDescription) {
    console.log(`[HomeIQ Cross-Device Bridge] Dispatched deep-link to Fire TV: ${uri} (${actionDescription})`);
    // In production, triggers AWS IoT Core MQTT / FCM push to Fire TV client
    return true;
}

// 1. LaunchRequest Handler ("Alexa, open HomeIQ")
const LaunchRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'LaunchRequest';
    },
    handle(handlerInput) {
        sendFireTvDeepLink(DEEP_LINKS.LAUNCH, 'Launch HomeIQ Dashboard');
        const speakOutput = "Welcome to HomeIQ. I've launched your smart home dashboard on Fire TV. You have 13 active devices across 4 rooms, and all 8 perimeter security beams are armed. What would you like to check?";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt("You can say, show my energy report, turn on eco mode, or check family safety.")
            .withStandardCard(
                "HomeIQ — Fire TV Dashboard",
                "Launched HomeIQ on your Fire TV.\n13 Devices | 4 Rooms | Security Armed",
                "https://raw.githubusercontent.com/LAKKIREDDY-2001/HomeIQ/main/docs/screenshots/01_main_dashboard.png"
            )
            .getResponse();
    }
};

// 2. EnergyReportIntent Handler ("Alexa, show my energy report")
const EnergyReportIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'EnergyReportIntent';
    },
    handle(handlerInput) {
        sendFireTvDeepLink(DEEP_LINKS.ENERGY_REPORT, 'Open Energy Analytics Spline Screen');
        const speakOutput = "Opening Energy Insights on your Fire TV now. This week's total consumption is 103.4 kilowatt hours, down 8% compared to last week. Your peak load was 4.8 kilowatts at 6 PM.";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .withStandardCard(
                "Energy Insights (103.4 kWh)",
                "Weekly consumption down 8%.\nEstimated cost: ₹14.48 ($14.48).\nPeak usage: 6 PM (4.8 kW)",
                "https://raw.githubusercontent.com/LAKKIREDDY-2001/HomeIQ/main/docs/screenshots/03_energy_analytics.png"
            )
            .getResponse();
    }
};

// 3. PowerUsageIntent Handler ("Alexa, what's my power usage today?")
const PowerUsageIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'PowerUsageIntent';
    },
    handle(handlerInput) {
        const speakOutput = "Today, your home has used 18.4 kilowatt hours, which is 61% of your daily 30 kilowatt hour budget. Your living room AC is responsible for 42% of that load.";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt("Would you like me to switch your AC to Eco Saver mode?")
            .getResponse();
    }
};

// 4. EcoModeIntent Handler ("Alexa, turn on eco mode")
const EcoModeIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'EcoModeIntent';
    },
    handle(handlerInput) {
        sendFireTvDeepLink(DEEP_LINKS.ECO_MODE, 'Activate Eco Saver 26°C');
        const speakOutput = "Eco Saver activated on Fire TV. Living room AC set to 26 degrees Celsius and unnecessary ceiling lights dimmed. You'll save approximately ₹85 this month.";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .withSimpleCard("Eco Mode Activated", "AC set to 26°C | Projected savings: ₹85/month")
            .getResponse();
    }
};

// 5. FamilySafetyIntent Handler ("Alexa, check family safety")
const FamilySafetyIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'FamilySafetyIntent';
    },
    handle(handlerInput) {
        sendFireTvDeepLink(DEEP_LINKS.FAMILY_GUARDIAN, 'Open Family Guardian Safety Vault');
        const speakOutput = "Attention: Family Guardian detected that the kitchen stove has been on for 3 hours with no motion in the kitchen. Elder Care monitoring has flagged this anomaly. Would you like me to auto-shutoff the stove?";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt("Say 'shut off stove' to turn it off immediately.")
            .withStandardCard(
                "⚠ Family Guardian Anomaly Detected",
                "Kitchen Stove ON for 3 hours with no motion detected.\nElder Care protection active.",
                "https://raw.githubusercontent.com/LAKKIREDDY-2001/HomeIQ/main/docs/screenshots/06_customer_safety_vault.png"
            )
            .getResponse();
    }
};

// 6. ShutoffStoveIntent Handler ("Alexa, shut off the kitchen stove")
const ShutoffStoveIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'ShutoffStoveIntent';
    },
    handle(handlerInput) {
        sendFireTvDeepLink(DEEP_LINKS.STOVE_SHUTOFF, 'Emergency Auto-Shutoff Kitchen Stove');
        const speakOutput = "Emergency shutoff executed. Kitchen stove has been powered off on Fire TV, and a safety confirmation has been sent to family members.";
        
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .withSimpleCard("Safety Action Executed", "Kitchen stove powered OFF via Family Guardian emergency circuit.")
            .getResponse();
    }
};

// Standard Handlers
const HelpIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.HelpIntent';
    },
    handle(handlerInput) {
        const speakOutput = "You can ask HomeIQ to show energy reports, check today's power usage, turn on eco mode, or monitor family safety alerts on your Fire TV.";
        return handlerInput.responseBuilder.speak(speakOutput).reprompt(speakOutput).getResponse();
    }
};

const CancelAndStopIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && (Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.CancelIntent'
                || Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.StopIntent');
    },
    handle(handlerInput) {
        return handlerInput.responseBuilder.speak("Goodbye from HomeIQ.").getResponse();
    }
};

const SessionEndedRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'SessionEndedRequest';
    },
    handle(handlerInput) {
        return handlerInput.responseBuilder.getResponse();
    }
};

const ErrorHandler = {
    canHandle() {
        return true;
    },
    handle(handlerInput, error) {
        console.error(`Error handled: ${error.message}`);
        return handlerInput.responseBuilder
            .speak("Sorry, I had trouble connecting to your HomeIQ Fire TV. Please try again.")
            .getResponse();
    }
};

exports.handler = Alexa.SkillBuilders.custom()
    .addRequestHandlers(
        LaunchRequestHandler,
        EnergyReportIntentHandler,
        PowerUsageIntentHandler,
        EcoModeIntentHandler,
        FamilySafetyIntentHandler,
        ShutoffStoveIntentHandler,
        HelpIntentHandler,
        CancelAndStopIntentHandler,
        SessionEndedRequestHandler
    )
    .addErrorHandlers(ErrorHandler)
    .lambda();
