from pathlib import Path

import pandas as pd


def run(master_path: str, summary_path: str, mapping_path: str, outdir: Path) -> None:
    df = pd.read_csv(master_path)
    df = normalize(df, mapping_path)

    grouped = (
        df.groupby(["region_code", "region_name", "material_code"], dropna=False)
        .agg(
            total_generated_kg=("generated_kg", "sum"),
            total_recycled_kg=("recycled_kg", "sum"),
        )
        .reset_index()
    )
    grouped["recycle_rate"] = grouped["total_recycled_kg"] / grouped["total_generated_kg"].replace(0, pd.NA)
    grouped.to_csv(outdir / "indicators.csv", index=False)

    if summary_path:
        summary_df = pd.read_csv(summary_path)
        summary_df.to_csv(outdir / "summary_stats_copy.csv", index=False)


def normalize(df: pd.DataFrame, mapping_path: str) -> pd.DataFrame:
    df["generated_kg"] = df["generated_kg"].fillna(0)
    df["recycled_kg"] = df["recycled_kg"].fillna(0)
    df["data_quality_flag"] = df["data_quality_flag"].fillna("OK")

    if mapping_path:
        mp = pd.read_csv(mapping_path)
        mapping = dict(zip(mp["original_label"], mp["mapped_label"]))
        df["material_code"] = df["material_code"].map(mapping).fillna(df["material_code"])

    return df

