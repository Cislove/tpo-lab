import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
import sys

# ==================== НАСТРОЙКИ ====================
# Укажите пути к вашим CSV файлам здесь
CONFIG_1_FILE = "result/config-1/results.csv"
CONFIG_2_FILE = "result/config-2/results.csv"
CONFIG_3_FILE = "result/config-3/results.csv"
STRESS_TEST_FILE = "result/stress-normal/result.csv"

# Названия конфигураций
CONFIG_LABELS = ["Config 1", "Config 2", "Config 3"]
STRESS_LABEL = "Stress Test"

# Пути для сохранения результатов
OUTPUT_DIR = "resource/load"
RESPONSE_TIME_GRAPH_PATH = os.path.join(OUTPUT_DIR, "response_time_graph.png")
AGGREGATE_GRAPH_PATH = os.path.join(OUTPUT_DIR, "aggregate_graph.png")
LOAD_VS_RT_GRAPH_PATH = os.path.join(OUTPUT_DIR, "load_vs_response_time.png")
MARKDOWN_TABLE_PATH = os.path.join(OUTPUT_DIR, "response_times.md")

# Размер окна усреднения для графика нагрузки (в секундах)
AVG_WINDOW_SECONDS = 10

# Параметр сглаживания для графика стресс-теста (скользящее среднее)
# Чем больше число, тем более плавным будет график, но тем меньше деталей.
# Рекомендуется от 3 до 10.
SMOOTHING_WINDOW = 5

# Порог времени отклика для анализа деградации (мс)
RT_THRESHOLD_MS = 460
# ===================================================

def process_csv(file_path):
    """Читает и обрабатывает CSV файл с результатами тестирования"""
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"Файл не найден: {file_path}")

    df = pd.read_csv(file_path)
    df['elapsed'] = pd.to_numeric(df['elapsed'], errors='coerce')
    df = df.dropna(subset=['elapsed'])
    df['datetime'] = pd.to_datetime(df['timeStamp'], unit='ms')

    first_timestamp = df['timeStamp'].min()
    df['relative_time'] = (df['timeStamp'] - first_timestamp) / 1000.0

    return df

def plot_response_time_graph(dfs, labels, output_path):
    """Строит график Response Time Over Time с относительным временем"""
    plt.figure(figsize=(14, 7))

    for df, label in zip(dfs, labels):
        plt.plot(df['relative_time'], df['elapsed'], label=label, alpha=0.7, linewidth=1)

    plt.xlabel('Time (seconds from start)', fontsize=12)
    plt.ylabel('Response Time (ms)', fontsize=12)
    plt.title('Response Time Over Time', fontsize=14, fontweight='bold')
    plt.legend(loc='best')
    plt.grid(True, alpha=0.3)
    plt.tight_layout()

    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    plt.close()
    print(f"✓ График 'Response Time Over Time' сохранен как {output_path}")

def plot_aggregate_graph(dfs, labels, output_path):
    """Строит столбчатую диаграмму с агрегированными статистиками"""
    stats = []
    for df, label in zip(dfs, labels):
        avg = df['elapsed'].mean()
        min_val = df['elapsed'].min()
        max_val = df['elapsed'].max()
        percentile_95 = np.percentile(df['elapsed'], 95)
        stats.append({
            'Config': label,
            'Average': avg,
            'Min': min_val,
            'Max': max_val,
            '95th Percentile': percentile_95
        })

    stats_df = pd.DataFrame(stats)

    x = np.arange(len(labels))
    width = 0.2

    fig, ax = plt.subplots(figsize=(14, 7))
    bars1 = ax.bar(x - width*1.5, stats_df['Average'], width, label='Average', color='#2196F3')
    bars2 = ax.bar(x - width/2, stats_df['Min'], width, label='Min', color='#4CAF50')
    bars3 = ax.bar(x + width/2, stats_df['Max'], width, label='Max', color='#F44336')
    bars4 = ax.bar(x + width*1.5, stats_df['95th Percentile'], width, label='95th Percentile', color='#FF9800')

    for bars in [bars1, bars2, bars3, bars4]:
        for bar in bars:
            height = bar.get_height()
            ax.annotate(f'{height:.0f}',
                       xy=(bar.get_x() + bar.get_width() / 2, height),
                       xytext=(0, 3),
                       textcoords="offset points",
                       ha='center', va='bottom', fontsize=8)

    ax.set_xlabel('Configuration', fontsize=12)
    ax.set_ylabel('Response Time (ms)', fontsize=12)
    ax.set_title('Aggregate Response Time Statistics', fontsize=14, fontweight='bold')
    ax.set_xticks(x)
    ax.set_xticklabels(labels)
    ax.legend(loc='best')
    ax.grid(True, alpha=0.3, axis='y')

    plt.tight_layout()
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    plt.close()
    print(f"✓ График 'Aggregate Statistics' сохранен как {output_path}")

def plot_stress_load_vs_rt(df, label, output_path, window_seconds=AVG_WINDOW_SECONDS, smoothing=SMOOTHING_WINDOW):
    """
    Строит график зависимости времени отклика от нагрузки (RPS) для стресс-теста.
    Применяет сглаживание (скользящее среднее) для устранения шумов.
    Ось Y начинается с 0.
    """
    plt.figure(figsize=(14, 7))

    if not df.empty:
        temp_df = df.copy()

        max_time = temp_df['relative_time'].max()
        bins = np.arange(0, max_time + window_seconds, window_seconds)

        temp_df['bin'] = pd.cut(temp_df['relative_time'], bins=bins, labels=False)

        agg_df = temp_df.groupby('bin').agg(
            avg_rt=('elapsed', 'mean'),
            count=('elapsed', 'count')
        ).reset_index()

        agg_df['rps'] = agg_df['count'] / window_seconds
        agg_df = agg_df.dropna(subset=['avg_rt', 'rps'])

        # Сортируем по нагрузке для корректного построения линии
        agg_df = agg_df.sort_values(by='rps').reset_index(drop=True)

        # --- Сглаживание данных ---
        if smoothing > 1 and len(agg_df) > smoothing:
            # Используем скользящее среднее (rolling mean)
            # min_periods=1 позволяет строить график даже для начальных точек, где окно еще не заполнено
            agg_df['smoothed_rt'] = agg_df['avg_rt'].rolling(window=smoothing, min_periods=1).mean()
            y_values = agg_df['smoothed_rt']
        else:
            y_values = agg_df['avg_rt']

        plt.plot(agg_df['rps'], y_values, marker='o', markersize=4, label=label, color='#E91E63', alpha=0.8, linewidth=2)

    plt.xlabel('Load (Requests Per Second - RPS)', fontsize=12)
    plt.ylabel('Average Response Time (ms)', fontsize=12)
    plt.title(f'Stress Test: Response Time vs Load (Smoothed: {smoothing})', fontsize=14, fontweight='bold')
    plt.legend(loc='best')
    plt.grid(True, alpha=0.3)
    plt.ylim(bottom=0) # Начинаем ось Y с 0
    plt.tight_layout()

    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    plt.close()
    print(f"✓ График 'Stress Test Load vs RT' сохранен как {output_path}")

def analyze_stress_test(df, label):
    """
    Анализирует стресс-тест: считает статистику и находит момент превышения порога RT.
    """
    # Статистика
    avg = df['elapsed'].mean()
    median = df['elapsed'].median()
    p75 = np.percentile(df['elapsed'], 75)
    p90 = np.percentile(df['elapsed'], 90)
    p95 = np.percentile(df['elapsed'], 95)
    p99 = np.percentile(df['elapsed'], 99)
    min_val = df['elapsed'].min()
    max_val = df['elapsed'].max()
    std_dev = df['elapsed'].std()
    count = len(df)

    table_row = [
        label,
        f"{avg:.2f}",
        f"{median:.2f}",
        f"{p75:.2f}",
        f"{p90:.2f}",
        f"{p95:.2f}",
        f"{p99:.2f}",
        f"{min_val:.2f}",
        f"{max_val:.2f}",
        f"{std_dev:.2f}",
        f"{count}"
    ]

    # Поиск первого превышения порога RT
    threshold_mask = df['elapsed'] > RT_THRESHOLD_MS

    threshold_info = ""
    if threshold_mask.any():
        first_threshold_idx = threshold_mask.idxmax()
        first_threshold_row = df.loc[first_threshold_idx]
        users_at_threshold = first_threshold_row['allThreads']
        time_at_threshold = first_threshold_row['relative_time']
        rt_at_threshold = first_threshold_row['elapsed']

        threshold_info = f"RT > {RT_THRESHOLD_MS}ms at {users_at_threshold} users (Time: {time_at_threshold:.1f}s, RT: {rt_at_threshold}ms)"
        print(f"\n⚠️ ВАЖНО: {threshold_info}")
    else:
        print(f"\n✅ Время отклика никогда не превышало {RT_THRESHOLD_MS} мс.")

    return table_row, threshold_info

def create_markdown_table(dfs, labels, stress_df, stress_label, output_path):
    """Создает Markdown таблицу со статистиками для всех конфигов и стресс-теста"""
    table_data = []

    for df, label in zip(dfs, labels):
        avg = df['elapsed'].mean()
        median = df['elapsed'].median()
        p75 = np.percentile(df['elapsed'], 75)
        p90 = np.percentile(df['elapsed'], 90)
        p95 = np.percentile(df['elapsed'], 95)
        p99 = np.percentile(df['elapsed'], 99)
        min_val = df['elapsed'].min()
        max_val = df['elapsed'].max()
        std_dev = df['elapsed'].std()
        count = len(df)

        table_data.append([
            label,
            f"{avg:.2f}",
            f"{median:.2f}",
            f"{p75:.2f}",
            f"{p90:.2f}",
            f"{p95:.2f}",
            f"{p99:.2f}",
            f"{min_val:.2f}",
            f"{max_val:.2f}",
            f"{std_dev:.2f}",
            f"{count}"
        ])

    if stress_df is not None and not stress_df.empty:
        stress_row, _ = analyze_stress_test(stress_df, stress_label)
        table_data.append(stress_row)

    md_table = "| Configuration | Average | Median | 75th % | 90th % | 95th % | 99th % | Min | Max | Std Dev | Count |\n"
    md_table += "|---------------|---------|--------|--------|--------|--------|--------|-----|-----|---------|-------|\n"

    for row in table_data:
        md_table += f"| {row[0]} | {row[1]} | {row[2]} | {row[3]} | {row[4]} | {row[5]} | {row[6]} | {row[7]} | {row[8]} | {row[9]} | {row[10]} |\n"

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(md_table)

    print(f"✓ Markdown таблица сохранена как {output_path}")
    return md_table

def main():
    """Основная функция выполнения"""
    print("=" * 60)
    print("Анализ результатов нагрузочного и стресс-тестирования")
    print("=" * 60)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # 1. Обработка обычных конфигов
    config_files = [CONFIG_1_FILE, CONFIG_2_FILE, CONFIG_3_FILE]
    missing_files = [f for f in config_files if not os.path.exists(f)]
    if missing_files:
        print("\n❌ Ошибка: Следующие файлы конфигов не найдены:")
        for f in missing_files:
            print(f"   - {f}")
        return

    dfs = []
    for i, file_path in enumerate(config_files):
        print(f"\n📊 Обработка конфига {i+1}/3: {file_path}")
        try:
            df = process_csv(file_path)
            dfs.append(df)
            print(f"   ✓ Загружено {len(df)} записей")
        except Exception as e:
            print(f"   ❌ Ошибка: {e}")
            return

    # 2. Обработка стресс-теста
    stress_df = None
    if os.path.exists(STRESS_TEST_FILE):
        print(f"\n🔥 Обработка стресс-теста: {STRESS_TEST_FILE}")
        try:
            stress_df = process_csv(STRESS_TEST_FILE)
            print(f"   ✓ Загружено {len(stress_df)} записей")

            plot_stress_load_vs_rt(stress_df, STRESS_LABEL, LOAD_VS_RT_GRAPH_PATH)

        except Exception as e:
            print(f"   ❌ Ошибка при обработке стресс-теста: {e}")
    else:
        print(f"\n⚠️ Файл стресс-теста не найден: {STRESS_TEST_FILE}. Пропускаю.")

    print("\n" + "=" * 60)
    print("Генерация отчетов...")
    print("=" * 60)

    plot_response_time_graph(dfs, CONFIG_LABELS, RESPONSE_TIME_GRAPH_PATH)
    plot_aggregate_graph(dfs, CONFIG_LABELS, AGGREGATE_GRAPH_PATH)

    md_table = create_markdown_table(dfs, CONFIG_LABELS, stress_df, STRESS_LABEL, MARKDOWN_TABLE_PATH)

    print("\n" + "=" * 60)
    print("✅ Анализ завершен успешно!")
    print("=" * 60)
    print("\nСозданные файлы:")
    print(f"  1. {RESPONSE_TIME_GRAPH_PATH} - График изменения времени ответа (Конфиги)")
    print(f"  2. {AGGREGATE_GRAPH_PATH}     - Столбчатая диаграмма статистик (Конфиги)")
    if stress_df is not None:
        print(f"  3. {LOAD_VS_RT_GRAPH_PATH}    - Зависимость времени отклика от нагрузки (Стресс-тест, сглаженный)")
    print(f"  4. {MARKDOWN_TABLE_PATH}       - Markdown таблица со статистиками")

if __name__ == "__main__":
    main()