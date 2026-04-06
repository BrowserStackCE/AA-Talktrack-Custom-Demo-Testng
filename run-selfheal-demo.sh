#!/bin/bash
# =============================================================================
# BrowserStack Self-Heal Demo — Single Jenkins Build Script
# =============================================================================
# This script automates the full self-heal demo flow in a single run:
#
# DEMO PART 1 (Without Self-Healing): selfHeal: false
#   Step 1: BaseApp + selfHeal:false  → Tests PASS  (baseline)
#   Step 2: SelfHealApp + selfHeal:false → Tests FAIL (broken selectors)
#
# DEMO PART 2 (With Self-Healing): selfHeal: true
#   Step 3: BaseApp + selfHeal:true   → Tests PASS  (Agent learns)
#   Step 4: SelfHealApp + selfHeal:true → Tests PASS (AI heals selectors)
#
# Jenkins Setup:
#   1. Add this as a "Execute Shell" build step in Jenkins
#   2. Set the following Jenkins credentials/env vars:
#      - BROWSERSTACK_USERNAME
#      - BROWSERSTACK_ACCESS_KEY
#   3. Run: chmod +x run-selfheal-demo.sh && ./run-selfheal-demo.sh
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BSTACK_YML="$SCRIPT_DIR/src/test/resources/conf/browserstack-selfheal.yml"
BASE_APP="./apps/BaseApp.apk"
SELFHEAL_APP="./apps/SelfHealApp.apk"

# Export BROWSERSTACK_CONFIG_FILE so Maven picks it up via ${env.BROWSERSTACK_CONFIG_FILE}
export BROWSERSTACK_CONFIG_FILE="$BSTACK_YML"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log()     { echo -e "${BLUE}[INFO]${NC} $1"; }
pass()    { echo -e "${GREEN}[PASS]${NC} $1"; }
fail()    { echo -e "${RED}[FAIL]${NC} $1"; }
section() {
    echo -e "\n${YELLOW}========================================${NC}"
    echo -e "${YELLOW} $1${NC}"
    echo -e "${YELLOW}========================================${NC}"
}

log "Using BrowserStack config: $BROWSERSTACK_CONFIG_FILE"

# Helper: update app and selfHeal in browserstack-selfheal.yml
set_config() {
    local app="$1"
    local self_heal="$2"

    # Deactivate both app lines first
    sed -i.bak "s|^app:.*BaseApp.*|# app: $BASE_APP          # BaseApp — original selectors|g" "$BSTACK_YML"
    sed -i.bak "s|^app:.*SelfHealApp.*|# app: $SELFHEAL_APP   # SelfHealApp — changed selectors|g" "$BSTACK_YML"

    # Activate the selected app
    if [ "$app" = "base" ]; then
        sed -i.bak "s|^# app: $BASE_APP.*|app: $BASE_APP          # BaseApp — original selectors|g" "$BSTACK_YML"
    else
        sed -i.bak "s|^# app: $SELFHEAL_APP.*|app: $SELFHEAL_APP   # SelfHealApp — changed selectors|g" "$BSTACK_YML"
    fi

    # Update selfHeal value
    sed -i.bak "s|^selfHeal:.*|selfHeal: $self_heal  # Managed automatically by run-selfheal-demo.sh|g" "$BSTACK_YML"
    rm -f "$BSTACK_YML.bak"

    log "Config updated → app=$app, selfHeal=$self_heal"
}

cd "$SCRIPT_DIR"

# Track overall result
OVERALL_PASS=true

# =============================================================================
section "DEMO PART 1 — Without Self-Healing (selfHeal: false)"
# =============================================================================

# Step 1: BaseApp + selfHeal:false → PASS
section "Step 1: BaseApp + selfHeal:false (Expected: PASS)"
set_config "base" "false"
set +e
mvn test -P sampleBaseAppTest -q 2>&1 | tee /tmp/selfheal_step1.log
STEP1_EXIT=$?
set -e
if [ $STEP1_EXIT -eq 0 ]; then
    pass "Step 1 PASSED — BaseApp tests pass with original selectors ✓"
else
    fail "Step 1 FAILED unexpectedly"
    grep -E "(Tests run|FAILURE|ERROR)" /tmp/selfheal_step1.log | head -5
    OVERALL_PASS=false
fi

# Step 2: SelfHealApp + selfHeal:false → FAIL (expected)
section "Step 2: SelfHealApp + selfHeal:false (Expected: FAIL — broken selectors)"
set_config "selfheal" "false"
set +e
mvn test -P sampleSelfHealAppTest -q 2>&1 | tee /tmp/selfheal_step2.log
STEP2_EXIT=$?
set -e
if [ $STEP2_EXIT -ne 0 ]; then
    pass "Step 2 FAILED as expected — broken selectors cause failures without self-heal ✓"
    grep -E "(Tests run|FAILURE)" /tmp/selfheal_step2.log | head -3
else
    fail "Step 2 unexpectedly PASSED — SelfHealApp should fail without self-heal"
fi

# =============================================================================
section "DEMO PART 2 — With Self-Healing (selfHeal: true)"
# =============================================================================

# Step 3: BaseApp + selfHeal:true → PASS (Agent learns)
section "Step 3: BaseApp + selfHeal:true (Expected: PASS — Agent captures context)"
set_config "base" "true"
set +e
mvn test -P sampleBaseAppTest -q 2>&1 | tee /tmp/selfheal_step3.log
STEP3_EXIT=$?
set -e
if [ $STEP3_EXIT -eq 0 ]; then
    pass "Step 3 PASSED — Agent captured success context from BaseApp ✓"
else
    fail "Step 3 FAILED"
    grep -E "(Tests run|FAILURE|ERROR)" /tmp/selfheal_step3.log | head -5
    OVERALL_PASS=false
fi

# Step 4: SelfHealApp + selfHeal:true → PASS (AI heals)
section "Step 4: SelfHealApp + selfHeal:true (Expected: PASS — Self-Heal AI heals selectors)"
set_config "selfheal" "true"
set +e
mvn test -P sampleSelfHealAppTest -q 2>&1 | tee /tmp/selfheal_step4.log
STEP4_EXIT=$?
set -e
if [ $STEP4_EXIT -eq 0 ]; then
    pass "Step 4 PASSED — Self-Heal AI successfully healed all broken selectors! ✓"
else
    fail "Step 4 FAILED — Self-Heal could not heal all selectors"
    grep -E "(Tests run|FAILURE|ERROR)" /tmp/selfheal_step4.log | head -5
    OVERALL_PASS=false
fi

# =============================================================================
section "Restoring browserstack-selfheal.yml to default state"
# =============================================================================
set_config "base" "false"
log "Restored: app=BaseApp, selfHeal=false"

# =============================================================================
section "DEMO SUMMARY"
# =============================================================================
echo ""
echo "  Part 1 Step 1 (BaseApp,     selfHeal:false) → $([ $STEP1_EXIT -eq 0 ] && echo 'PASS ✓' || echo 'FAIL ✗')"
echo "  Part 1 Step 2 (SelfHealApp, selfHeal:false) → $([ $STEP2_EXIT -ne 0 ] && echo 'FAIL as expected ✓' || echo 'UNEXPECTED PASS')"
echo "  Part 2 Step 3 (BaseApp,     selfHeal:true)  → $([ $STEP3_EXIT -eq 0 ] && echo 'PASS ✓' || echo 'FAIL ✗')"
echo "  Part 2 Step 4 (SelfHealApp, selfHeal:true)  → $([ $STEP4_EXIT -eq 0 ] && echo 'PASS ✓' || echo 'FAIL ✗')"
echo ""
log "View results: https://app-automate.browserstack.com/dashboard/v2"
echo ""

if [ "$OVERALL_PASS" = true ]; then
    pass "All demo steps completed successfully!"
    exit 0
else
    fail "One or more demo steps failed. Check logs above."
    exit 1
fi