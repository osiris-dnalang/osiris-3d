# Osiris Governance — Android Control Plane

An Android application built with Kotlin and Jetpack Compose providing the deterministic capability governor, cryptographic canonical hash binding, dynamic non-substitution evidence ledger, and release authority boundary for the OSIRIS ecosystem.

## Core Architectural Invariants

1. **Cognition Proposes; Governance Authorizes**: No cognitive substrate possesses execution authority. Substrates generate serialized `ActionProposal` records only.
2. **Deterministic Byte Binding**: Authorization decisions bind strictly to the exact serialized canonical bytes of the proposal via SHA-256 (`OSIRIS-CANONICAL-JSON-V1`). Any byte modification invalidates the binding.
3. **Fail-Closed Execution**: Action proposals are denied unless explicitly permitted by policy. All denials occur *prior* to adapter invocation.
4. **Zero Filesystem Authority in Replay**: In the offline replay slice, execution tokens are typed `ReplayExecutionPermit` with scope `REPLAY_ONLY_NON_CRYPTOGRAPHIC`, conferring zero authority for real-world side effects.
5. **Non-Substitution Invariant**: `Evidence Class A -/-> Claim Class B`. Adjudication Precedence: `FALSE > BLOCKED > UNVERIFIED > TRUE`.

## Application Modules & Screens

- **Console / LIVLM Beta**: Interactive natural language intent interpreter, live capability governor, process epoch management, and deterministic fixture replay runner (`echo-v1`, `benchmark-sample-v1`, `fixture-energy-test-v1`).
- **Dynamic Evidence Ledger**: Append-only cryptographic hash chain with unbroken chain verification from genesis digest to head, filtering across evidence planes (`REPRODUCIBILITY`, `RUNTIME_SECURITY`, `SCIENTIFIC_EXPERIMENTAL`, `HARDWARE_EXECUTION`, `GOVERNANCE`).
- **Claim Evaluator**: Real-time non-substitution adjudication engine testing claims against cited ledger events, detecting plane mismatches, scope divergences, and transitive claim laundering.
- **Confinement & Release Gate**: 30-probe Confinement Matrix (10 mandatory criteria A through J across 3 fresh runs), mechanical `S_deployed` adjudication, and Master Release Gate enforcing fail-closed release prerequisites.
- **Canonical Inspector**: Real-time `OSIRIS-CANONICAL-JSON-V1` validation and SHA-256 hash generator detecting prohibited floats, duplicate keys, BOM, and forbidden bidirectional control codepoints.

## Build & Tech Stack

- **Platform**: Android SDK 36, Kotlin 2.2.10, Gradle 9.3.1, AGP 9.1.1
- **UI Framework**: Jetpack Compose with Material 3 Design System
- **State Management**: Android Architecture Components (ViewModel + StateFlow)
- **Serialization**: Kotlinx Serialization JSON & Canonical Byte Serializer
