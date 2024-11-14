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

const val DROP_DOWN = "javascript:(function() {\n" +
        "    Android.logMessage('Select the dropdown element by a specific identifier');\n" +
        "    var dropdown = document.querySelector('select[id=\"address-level1\"]');\n" +
        "    if (dropdown) {\n" +
        "        Android.logMessage('Print the current selected value');\n" +
        "        var selectedValue = dropdown.options[dropdown.selectedIndex].value;\n" +
        "        Android.logMessage('Current selected value:'+selectedValue);\n" +
        "        Android.logMessage('Alternatively, print the text of the selected option');\n" +
        "        var selectedText = dropdown.options[dropdown.selectedIndex].text;\n" +
        "        Android.logMessage('Current selected text: '+ selectedText);\n" +
        "       Android.logMessage('Print all values in the dropdown');\n" +
        "        Android.logMessage('All dropdown values:');\n" +
        "        for (var i = 0; i < dropdown.options.length; i++) {\n" +
        "            var option = dropdown.options[i];\n" +
        "             Android.logMessage('Value: '+ option.value+ ' Text: '+ option.text);\n" +
        "        }\n"+
        "        Android.logMessage('To set or change the value of the dropdown');\n" +
        "        var valueToSelect = 'GA'; \n" +
        "        var optionToSelect = dropdown.querySelector('option[value=\"' + valueToSelect + '\"]');\n" +
        "        if (optionToSelect) {\n" +
        "            optionToSelect.selected = true;\n" +
        "            var newSelectedValue = dropdown.options[dropdown.selectedIndex].value;\n" +
        "            Android.logMessage('Updated selected value:'+newSelectedValue);\n" +
        "        } else {\n" +
        "             Android.logMessage('Option value to select not found:'+ valueToSelect);\n" +
        "        }\n" +
        "    } else {\n" +
        "        Android.logMessage('Dropdown not found');\n" +
        "    }\n" +
        "})();"