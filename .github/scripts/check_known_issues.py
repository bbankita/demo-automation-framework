#!/usr/bin/env python3
"""
Reads and analyzes Cucumber JSON reports to categorize known-issues excluded from regression.

Determines if a failure falls into one of three categories:
1. A fixed known issue that should be re-added to the main regression suite.
2. A precondition step failure that requires automated script fixes.
3. A valid known issue currently associated with an existing open bug report.

exits 0 if all known issues are still valid, or 1 if any known issues are stale or broken.
"""
import sys
import json

EXPECTED = "expected"
STALE = "stale"
BROKEN = "broken"

def load_report(path):
    """
    Return the report's list of features, or None if it can't be read.
    """
    try:
        with open(path, encoding="utf-8") as f:
            cucumber_json = json.load(f)
        return cucumber_json
    except (ValueError, OSError) as e:
        print(f"::error::Error loading report from {path}: {e}")
        return None

def issue_tag(scenario):
    """
    Return the issue tag for a scenario, or None if it doesn't have one.
    """
    tags = scenario.get("tags", [])
    for tag in tags:
        if tag.get("name", "").startswith("@issue-"):
            return tag.get("name")
    return None

def repo_path(uri):
    """
    Return the path to the file in the repository, given its URI.
    """
    return uri.replace("classpath:", "src/test/resources/")

def status(item):
    return item["result"]["status"]

def classify(background, scenario):
    """
    Classify a scenario based on its status and the status of its background.
    Returns (group, reason)
    """
    setup_steps = scenario.get("before", []) + (background["steps"] if background else [])
    for step in setup_steps:
        if status(step) != "passed":
            return BROKEN, f"Setup failed before the scenario started, status: {status(step)}"
    keywords = [step["keyword"].strip() for step in scenario.get("steps", [])]
    if "When" not in keywords:
        return BROKEN, "There's no When step, so a failure can't be told apart from a setup problem"
    index = keywords.index("When")
    for step in scenario.get("steps", [])[:index+1]:
        if status(step) != "passed":
            return BROKEN, f"Failed at or before When. Step: {step['name']} {status(step)}"
    for step in scenario.get("steps",[])[index+1:]:
        if status(step) == "failed":
            return EXPECTED, f"Failed at step {step['name']}. Bug still persists"
        if status(step) != "passed":
            return BROKEN, f"{step['name']} has unexpected status {status(step)}"
    return STALE, "It passes now, so remove @known-issue if the bug is fixed"


def main():
    if len(sys.argv) != 2:
        print("Usage: check_known_issues.py <cucumber_json_file>")
        return 1
    cucumber_json_file = sys.argv[1]
    print(f"Checking {cucumber_json_file}")
    cucumber_json = load_report(cucumber_json_file)
    if cucumber_json is None:
        return 1
    print(f"Loaded {len(cucumber_json)} features from the report.")
    counts = {EXPECTED: 0, STALE: 0, BROKEN: 0}
    for feature in cucumber_json:
        background = None
        for element in feature.get("elements", []):
            if(element.get("type") == "background"):
                background = element
                continue
            scenario_name = element.get("name", "Unnamed Scenario")
            group, reason = classify(background, element)
            tag = issue_tag(element)
            if group == EXPECTED and tag is None:
                group,reason = BROKEN, "expected failure without @issue-<number> tag"
            counts[group] += 1
            if group == EXPECTED:
                print(f"Expected failure: {repo_path(feature['uri'])}:{element['line']} [{tag or 'no issue tag'}] {scenario_name} {reason}")
            else:
                print(f"::error file={repo_path(feature['uri'])},line={element['line']}:: {group.capitalize()}: {scenario_name} [{tag or 'no issue tag'}] {reason}")
    total = sum(counts.values())
    if(total == 0):
        print("::error::No scenarios found in the report.")
        return 1

    print(f"::notice::Total scenarios: {total}, Expected: {counts[EXPECTED]}, Stale: {counts[STALE]}, Broken: {counts[BROKEN]}")
    if(counts[BROKEN]>0 or counts[STALE]>0):
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())

