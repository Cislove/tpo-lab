import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
import sys

# ==================== НАСТРОЙКИ ====================
# Укажите пути к вашим CSV файлам здесь
CONFIG_1_FILE = "result/config-1/results.csv"  # Путь к файлу первой конфигурации
CONFIG_2_FILE = "result/config-2/results.csv"  # Путь к файлу второй конфигурации
CONFIG_3_FILE = "result/config-3/results.csv"  # Путь к файлу третьей конфигурации

# Названия конфигураций для отображения на графиках
CONFIG_LABELS = ["Config 1", "Config 2", "Config 3"]

# Пути для сохранения результатов
OUTPUT_DIR = "resource/load"  # Директория для сохранения файлов
RESPONSE_TIME_GRAPH_PATH = os.path.join(OUTPUT_DIR, "response_time_graph.png")
AGGREGATE_GRAPH_PATH = os.path.join(OUTPUT_DIR, "aggregate_graph.png")
MARKDOWN_TABLE_PATH = os.path.join(OUTPUT_DIR, "response_times.md")
# ===================================================

def process_csv(file_path):
    """Читает и обрабатывает CSV файл с результатами тестирования"""
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"Файл не найден: {file_path}")
    
    # Читаем CSV файл
    df = pd.read_csv(file_path)
    
    # Преобразуем elapsedTime в числовой формат
    df['elapsed'] = pd.to_numeric(df['elapsed'], errors='coerce')
    
    # Удаляем строки с NaN значениями elapsed time
    df = df.dropna(subset=['elapsed'])
    
    # Преобразуем timeStamp в datetime для лучшей визуализации
    df['datetime'] = pd.to_datetime(df['timeStamp'], unit='ms')
    
    # Вычисляем относительное время (в секундах от начала первого запроса)
    first_timestamp = df['timeStamp'].min()
    df['relative_time'] = (df['timeStamp'] - first_timestamp) / 1000.0  # переводим в секунды
    
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
    
    # Сохраняем график
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
    
    # Добавляем значения на столбцы
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

def create_markdown_table(dfs, labels, output_path):
    """Создает Markdown таблицу с подробными статистиками"""
    table_data = []
    
    for df, label in zip(dfs, labels):
        avg = df['elapsed'].mean()
        median = df['elapsed'].median()
        p50 = median
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
    
    # Создаем Markdown таблицу
    md_table = "| Configuration | Average | Median | 75th % | 90th % | 95th % | 99th % | Min | Max | Std Dev | Count |\n"
    md_table += "|---------------|---------|--------|--------|--------|--------|--------|-----|-----|---------|-------|\n"
    
    for row in table_data:
        md_table += f"| {row[0]} | {row[1]} | {row[2]} | {row[3]} | {row[4]} | {row[5]} | {row[6]} | {row[7]} | {row[8]} | {row[9]} | {row[10]} |\n"
    
    # Сохраняем только таблицу в файл
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(md_table)
    
    print(f"✓ Markdown таблица сохранена как {output_path}")
    return md_table

def main():
    """Основная функция выполнения"""
    print("=" * 60)
    print("Анализ результатов нагрузочного тестирования")
    print("=" * 60)
    
    # Создаем директорию для вывода, если она не существует
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    # Список файлов конфигураций
    config_files = [CONFIG_1_FILE, CONFIG_2_FILE, CONFIG_3_FILE]
    
    # Проверяем существование всех файлов
    missing_files = [f for f in config_files if not os.path.exists(f)]
    if missing_files:
        print("\n❌ Ошибка: Следующие файлы не найдены:")
        for f in missing_files:
            print(f"   - {f}")
        print("\nПожалуйста, проверьте пути к файлам в настройках скрипта.")
        return
    
    # Обрабатываем каждый CSV файл
    dfs = []
    for i, file_path in enumerate(config_files):
        print(f"\n📊 Обработка файла {i+1}/3: {file_path}")
        try:
            df = process_csv(file_path)
            dfs.append(df)
            print(f"   ✓ Загружено {len(df)} записей")
            print(f"   ✓ Диапазон времени: {df['datetime'].min()} - {df['datetime'].max()}")
            print(f"   ✓ Среднее время ответа: {df['elapsed'].mean():.2f} ms")
        except Exception as e:
            print(f"   ❌ Ошибка при обработке файла: {e}")
            return
    
    print("\n" + "=" * 60)
    print("Генерация графиков и отчетов...")
    print("=" * 60)
    
    # Генерируем графики и таблицу
    plot_response_time_graph(dfs, CONFIG_LABELS, RESPONSE_TIME_GRAPH_PATH)
    plot_aggregate_graph(dfs, CONFIG_LABELS, AGGREGATE_GRAPH_PATH)
    md_table = create_markdown_table(dfs, CONFIG_LABELS, MARKDOWN_TABLE_PATH)
    
    print("\n" + "=" * 60)
    print("✅ Анализ завершен успешно!")
    print("=" * 60)
    print("\nСозданные файлы:")
    print(f"  1. {RESPONSE_TIME_GRAPH_PATH} - График изменения времени ответа")
    print(f"  2. {AGGREGATE_GRAPH_PATH}     - Столбчатая диаграмма статистик")
    print(f"  3. {MARKDOWN_TABLE_PATH}       - Markdown таблица со статистиками")

if __name__ == "__main__":
    main()