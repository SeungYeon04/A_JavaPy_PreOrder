from pathlib import Path

import pandas as pd


def run(master_path: str, facilities_path: str, outdir: Path) -> None:
    df = pd.read_csv(master_path)
    df["trade_volume_kg"] = df["trade_volume_kg"].fillna(0)
    demand = (
        df.groupby(["region_code", "region_name", "material_code"])["trade_volume_kg"]
        .sum()
        .reset_index()
    )

    if facilities_path and Path(facilities_path).exists():
        fac = pd.read_csv(facilities_path)
        fac["capacity_kg_per_month"] = fac["capacity_kg_per_month"].fillna(0)
        supply = (
            fac.groupby(["region_code", "region_name", "materials_handled"])
            .agg(supply_capacity_kg=("capacity_kg_per_month", "sum"))
            .reset_index()
        )
        # materials_handled가 다중일 때는 매핑 전처리 필요. 간단히 첫 값 사용.
        supply["material_code"] = supply["materials_handled"].str.split(",").str[0]
    else:
        supply = pd.DataFrame(columns=["region_code", "region_name", "material_code", "supply_capacity_kg"])

    merged = demand.merge(
        supply,
        how="left",
        left_on=["region_code", "region_name", "material_code"],
        right_on=["region_code", "region_name", "material_code"],
    )
    merged["supply_capacity_kg"] = merged["supply_capacity_kg"].fillna(0)
    merged["gap_kg"] = merged["trade_volume_kg"] - merged["supply_capacity_kg"]
    merged.to_csv(outdir / "gap_table.csv", index=False)

    top_gap = merged.sort_values("gap_kg", ascending=False).head(20)
    lines = [
        f"{r.region_name} / {r.material_code}: gap={r.gap_kg:,.1f} kg "
        f"(demand={r.trade_volume_kg:,.1f}, supply={r.supply_capacity_kg:,.1f})"
        for r in top_gap.itertuples()
    ]
    Path(outdir / "top_gap_list.txt").write_text("\n".join(lines), encoding="utf-8")

