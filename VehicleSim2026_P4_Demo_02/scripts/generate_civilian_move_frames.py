from pathlib import Path
import math
import shutil

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "images" / "civilian.png"
OUTPUT_DIR = ROOT / "images" / "civilian-move"
BACKUP_DIR = OUTPUT_DIR / "original"
FRAME_COUNT = 20
TARGET_SIZE = (45, 59)


def clamp_alpha(image: Image.Image) -> Image.Image:
    if image.mode != "RGBA":
        image = image.convert("RGBA")
    return image


def crop_alpha_bounds(image: Image.Image) -> Image.Image:
    alpha = image.getchannel("A")
    bbox = alpha.getbbox()
    if bbox is None:
        return image.copy()
    return image.crop(bbox)


def resize_to_fit(image: Image.Image, canvas_size: tuple[int, int], margin: int) -> Image.Image:
    max_width = max(1, canvas_size[0] - margin * 2)
    max_height = max(1, canvas_size[1] - margin * 2)
    scale = min(max_width / image.width, max_height / image.height)
    scaled_size = (
        max(1, round(image.width * scale)),
        max(1, round(image.height * scale)),
    )
    return image.resize(scaled_size, Image.Resampling.LANCZOS)


def generate_frame(base: Image.Image, frame_index: int, canvas_size: tuple[int, int]) -> Image.Image:
    phase = (2.0 * math.pi * frame_index) / FRAME_COUNT
    sway = math.sin(phase)
    bounce = math.sin(phase * 2.0)
    twist = math.cos(phase)

    stretch_x = 1.0 + 0.025 * bounce
    stretch_y = 1.0 - 0.02 * bounce
    warped = base.resize(
        (
            max(1, round(base.width * stretch_x)),
            max(1, round(base.height * stretch_y)),
        ),
        Image.Resampling.BICUBIC,
    )

    shear_x = 0.04 * twist
    combined = warped.transform(
        warped.size,
        Image.Transform.AFFINE,
        (
            1.0,
            shear_x,
            -shear_x * warped.height / 2.0,
            0.0,
            1.0,
            0.0,
        ),
        resample=Image.Resampling.BICUBIC,
    )

    angle = 4.0 * sway
    rotated = combined.rotate(angle, resample=Image.Resampling.BICUBIC, expand=True)

    frame = Image.new("RGBA", canvas_size, (0, 0, 0, 0))
    x = (canvas_size[0] - rotated.width) // 2 + round(2.0 * sway)
    y = (canvas_size[1] - rotated.height) // 2 + round(3.0 * bounce)
    frame.alpha_composite(rotated, (x, y))
    return frame


def main() -> None:
    source = clamp_alpha(Image.open(SOURCE))
    cropped = crop_alpha_bounds(source)
    rotated = cropped.rotate(90, expand=True)

    prepared = resize_to_fit(rotated, TARGET_SIZE, margin=2)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    BACKUP_DIR.mkdir(parents=True, exist_ok=True)
    shutil.copy2(SOURCE, BACKUP_DIR / SOURCE.name)
    for frame_index in range(FRAME_COUNT):
        frame = generate_frame(prepared, frame_index, TARGET_SIZE)
        frame.save(OUTPUT_DIR / f"civilian-move_{frame_index}.png")


if __name__ == "__main__":
    main()
