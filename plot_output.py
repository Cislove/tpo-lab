import argparse
import math
from pathlib import Path
from typing import List, Tuple, Optional

import matplotlib.pyplot as plt


FUNC_NAMES = [
    "sec",
    "csc",
    "cot",
    "log2",
    "log3",
    "log5",
    "log10",
    "mainFunction",
]


def parse_blocks(text: str) -> List[List[Tuple[float, Optional[float]]]]:
    """Parses blocks separated by header lines 'x, y'.

    Each block becomes list of (x, y) where y may be None for Nan.
    """
    blocks: List[List[Tuple[float, Optional[float]]]] = []
    cur: List[Tuple[float, Optional[float]]] = []

    for raw in text.splitlines():
        line = raw.strip()
        if not line:
            continue
        if line.lower().replace(" ", "") in {"x,y", "x,y;"} or line.lower() == "x, y":
            if cur:
                blocks.append(cur)
                cur = []
            continue

        parts = [p.strip() for p in line.split(",")]
        if len(parts) < 2:
            continue
        x_s, y_s = parts[0], parts[1]
        x = float(x_s)
        y: Optional[float]
        if y_s.lower() in {"nan", "null", "none"}:
            y = None
        else:
            try:
                y = float(y_s)
                if math.isnan(y):
                    y = None
            except ValueError:
                y = None
        cur.append((x, y))

    if cur:
        blocks.append(cur)

    return blocks


def plot_blocks(
    blocks: List[List[Tuple[float, Optional[float]]]],
    out_dir: Path,
    show: bool,
) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)

    for i, block in enumerate(blocks):
        name = FUNC_NAMES[i] if i < len(FUNC_NAMES) else f"f{i+1}"

        finite_x = [x for x, y in block if y is not None]
        finite_y = [y for _, y in block if y is not None]

        plt.figure(figsize=(10, 5))
        if finite_x:
            plt.plot(finite_x, finite_y, marker="o", linewidth=1.2)

        plt.title(name)
        plt.xlabel("x")
        plt.ylabel("y")
        plt.ylim(-10, 10)
        plt.grid(True, alpha=0.3)
        plt.tight_layout()

        out_file = out_dir / f"{i+1:02d}_{name}.png"
        plt.savefig(out_file, dpi=150)
        if show:
            plt.show()
        plt.close()


def main() -> int:
    ap = argparse.ArgumentParser(description="Plot function blocks from Gradle output file.")
    ap.add_argument("--input", default="output", help="Path to input file (default: ./output)")
    ap.add_argument("--out", default="plots", help="Output directory for PNGs (default: ./plots)")
    ap.add_argument("--show", action="store_true", help="Also show figures interactively")
    args = ap.parse_args()

    input_path = Path(args.input)
    out_dir = Path(args.out)

    text = input_path.read_text(encoding="utf-8")
    blocks = parse_blocks(text)

    if not blocks:
        raise SystemExit("No data blocks found. Expected repeated headers 'x, y'.")

    plot_blocks(blocks, out_dir=out_dir, show=args.show)
    print(f"Saved {len(blocks)} plot(s) to: {out_dir.resolve()}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

