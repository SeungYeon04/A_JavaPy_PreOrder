import argparse
import sys
from pathlib import Path

from core import indicators, charts, gap


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--master", required=True)
    parser.add_argument("--summary", default="")
    parser.add_argument("--facilities", default="")
    parser.add_argument("--mapping", default="")
    parser.add_argument("--outdir", required=True)
    args = parser.parse_args()

    outdir = Path(args.outdir)
    outdir.mkdir(parents=True, exist_ok=True)

    indicators.run(args.master, args.summary, args.mapping, outdir)
    charts.run(args.master, outdir)
    gap.run(args.master, args.facilities, outdir)
    return 0


if __name__ == "__main__":
    sys.exit(main())

