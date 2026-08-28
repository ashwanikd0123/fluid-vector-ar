# Fluid Vector AR: Augmented Reality (AR) Use Cases

**Core Objective:** Integration of ARCore with a custom 2D vector drawing engine to project, trace, and map spatial coordinates between the digital canvas and the physical world.

## 1. Digital Stenciling & Mural Tracing (The Virtual Projector)

* **Concept:** Projects 2D vector paths (drawn locally or imported as SVGs) onto physical surfaces like walls, papers, or canvases via the device camera.
* **Target Users:** Muralists, traditional painters, and street artists.
* **Value Proposition:** Eliminates the need for expensive, bulky physical projectors. Allows artists to trace exact proportions and scale designs in real-time, even in daylight environments where traditional projectors fail.

## 2. Commercial Preview: Tattoo & Decal Placement

* **Concept:** Anchors drawn vector layers onto physical objects or human skin using AR plane detection and image tracking.
* **Target Users:** Tattoo artists, automotive customizers, and merchandise designers.
* **Value Proposition:** Provides clients with a 1:1 scale preview of the final artwork on their body or vehicle before any permanent physical work begins. This increases client confidence and drastically reduces pre-production revision disputes.

## 3. Spatial Floor Plan Mapping (Physical to Vector)

* **Concept:** Utilizes AR plane detection to allow users to tap physical corners of a room. The inverse matrix transformation maps these 3D real-world coordinates directly into the 2D `CanvasGestureState`.
* **Target Users:** Architects, interior designers, and real estate agents.
* **Value Proposition:** Auto-generates mathematically accurate 2D vector blueprints simply by pointing the camera and walking around a space, replacing manual tape measurements and manual drafting.

## 4. 2.5D Holographic Vector Art

* **Concept:** Renders individual vector layers with dynamic Z-axis spacing in an AR environment.
* **Target Users:** Digital artists, exhibition curators, and AR hobbyists.
* **Value Proposition:** Transforms standard flat vector drawings into floating, interactive 3D holograms. Users experience a natural parallax effect by physically walking around or moving their device around the anchored artwork.

---

**Technical Implementation Requirements:**

* **AR Engine:** ARCore (Google Play Services for AR) paired with Sceneview-Compose.
* **Data Mapping:** Bi-directional synchronization between the 2D `EditorUiState` (layers/strokes) and 3D AR Scene Nodes (anchors/depth).