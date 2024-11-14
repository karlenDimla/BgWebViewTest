package com.bgwebviewtest.pirtest

const val TRAVERSE_DOM = "(function() {\n" +
        "    function traverseDOM(element) {\n" +
        "        // Gather details about the current element\n" +
        "        let details = {\n" +
        "            tagName: element.tagName,\n" +
        "            attributes: {},\n" +
        "            textContent: element.textContent.trim().substring(0, 30) // Preview first 30 chars\n" +
        "        };\n" +
        "\n" +
        "        // Extract attributes\n" +
        "        for (let attr of element.attributes) {\n" +
        "            details.attributes[attr.name] = attr.value;\n" +
        "        }\n" +
        "\n" +
        "        // Log the details to the Android interface\n" +
        "        Android.logMessage(JSON.stringify(details));\n" +
        "\n" +
        "        // Recursively traverse each child element\n" +
        "        for (let i = 0; i < element.children.length; i++) {\n" +
        "            traverseDOM(element.children[i]);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    // Start traversing from the root element\n" +
        "    traverseDOM(document.documentElement);\n" +
        "})();"

const val CLICK_BUTTON = "javascript:(function() {\" +\n" +
        "                            \"    var button = document.getElementById('submitBtn');\" +\n" +
        "                            \"    if (button) {\" +\n" +
        "                            \"        button.click();\" +\n" +
        "                            \"    } else {\" +\n" +
        "                            \"        console.log('Button not found');\" +\n" +
        "                            \"    }\" +\n" +
        "                            \"})()"

const val FILL_FORM = "javascript:(function() {\n" +
        "var divElement = document.querySelector('.card-body');\n" +
        "    if (divElement) {\n" +
        "        // The div exists, log its content\n" +
        "        Android.logMessage('result content:'+divElement.innerHTML);\n" +
        "    } else {\n" +
        "        // The div does not exist\n" +
        "        Android.logMessage('result does not exist.');\n" +
        "    }\n"+
        "    Android.logMessage('Fill in the form fields')\n" +
        "    document.querySelector('input[name=\"username\"]').value = 'testuser';\n" +
        "    Android.logMessage('Filled in username')\n" +
        "    document.querySelector('input[name=\"password\"]').value = 'password123';\n" +
        "    Android.logMessage('Filled in password')\n" +
        "    document.querySelector('form').submit();\n" +
        "\n" +
        "    Android.logMessage('Use MutationObserver to detect changes indicating submission result')\n" +
        "    var observer = new MutationObserver(function(mutations, observer) {\n" +
        "        Android.logMessage('Check if a specific result element appears or other DOM changes happen')\n" +
        "        var resultElement = document.querySelector('#result'); // Adjust selector based on actual changes\n" +
        "        if(resultElement) {\n" +
        "            Android.logMessage('Form submitted, processed result found: ', resultElement.innerText);\n" +
        "            observer.disconnect();  // Stop observing when result is detected\n" +
        "        }\n" +
        "    });\n" +
        "\n" +
        "   Android.logMessage('Start observing the body for any child changes (indicating result rendering)')\n" +
        "   observer.observe(document.body, { childList: true, subtree: true });\n" +
        "})();"
