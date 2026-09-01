# AI Usage

AI-assisted development was used during this assignment.

## How AI Was Used

AI was used for:

- architecture review;
- API client and Page Object design suggestions;
- test isolation review;
- investigation of API and UI failures;
- CI and documentation review.

AI was not treated as a source of truth. Suggestions were validated against runtime application behavior, source code where useful, and automated test execution.

## Example of AI-Generated Work That Was Modified

An early API implementation used `/api/booking/` for booking creation.

Runtime testing returned HTTP 308. Instead of enabling redirects globally, the request was corrected to the exact `/api/booking` endpoint.

This prevents redirects from hiding an incorrect API client contract.

A second example occurred in UI automation. An initial locator selected the first link named `Book Now`.

Runtime diagnostics showed that it matched the homepage hero link (`#booking`) instead of a room reservation link.

The locator was changed to target the reservation URL pattern:

    a[href^='/reservation/']

This ties the locator to the actual business action rather than ambiguous visible text.

## AI Risk Identified

A major risk of AI-assisted automation development is confident but incorrect assumptions about endpoints, payloads, selectors, validation rules, timing and library behavior.

For example, increasing the timeout for the failed `Book Now` interaction would not have fixed the issue because the locator was selecting the wrong element.

The mitigation used was evidence-driven verification:

1. Reproduce the failure.
2. Inspect runtime behavior.
3. Form a specific hypothesis.
4. Make the smallest relevant change.
5. Rerun the affected test.
6. Run the complete suite before considering the work complete.

AI accelerated investigation and implementation, while executable tests and runtime evidence remained the validation mechanism.
