import glob
import operator

# 조사할 디렉토리 경로
# ROOT_DIR = '/mnt/c/Dev/projects/mcmp/service-orchestrator'
ROOT_DIR = input('LOC를 체크할 루트 디렉토리 입력 : ').replace(" ", "")

# 조사할 확장자 목록
extensions = [
    '*.java',
    # '*.xml',
    '*.js',
    '*.jsp',
    '*.css',
    '*.py'
]

print(str(extensions) + ' 확장자 파일들만 체크')

# 무시할 디렉토리 경로 목록
ignore_paths = [
 
]

# 무시할 파일 목록
ignore_files = [
    
]

total_line_count = 0
total_file_count = 0
files_grabbed = []


# dictionary 를 value 기준으로 정렬된 tuple 로 변환
def dict_to_sorted_by_val(tmp_dict, reverse=False):
    return sorted(tmp_dict.items(), key=operator.itemgetter(1), reverse=reverse)


# 카운트 함수
def start_count():
    global total_line_count, total_file_count
    line_count_dict = dict()
    extension_count_dict = dict.fromkeys(extensions, 0)

    # 설정한 확장자가 포함된 파일 리스트 생성
    [files_grabbed.extend(glob.glob(f'{ROOT_DIR}/**/{extension}', recursive=True)) for extension in extensions]

    # 파일별로 라인수, 확장자별 갯수, 라인총합, 확장자별 갯수 총합 구함
    for file_name_with_path in files_grabbed:
        file_name = file_name_with_path.split('/')[-1]
        ext = file_name.split('.')[-1]
        if file_name in ignore_files:
            continue

        is_ignored = False
        for ignore_path in ignore_paths:
            if file_name_with_path.find(ignore_path) != -1:
                is_ignored = True
                break

        if is_ignored:
            continue

        extension_count_dict['*.' + ext] += 1

        line_count = sum(1 for line in open(file_name_with_path, encoding='ISO-8859-1'))
        line_count_dict[file_name_with_path] = line_count

        total_line_count += line_count
        total_file_count += 1

    # reverse=True 면 value 기준으로 내림차순 정렬
    sorted_line_count = dict_to_sorted_by_val(line_count_dict, reverse=True)
    sorted_file_count = dict_to_sorted_by_val(extension_count_dict, reverse=True)
    return sorted_line_count, sorted_file_count


# 카운트 함수 실행
line_count, file_count = start_count()

# 출력
for result in line_count:
    file = result[0]
    count = result[1]
    print('{:>4} {:<0}'.format(count, file))

print('\n지정한 확장자별 파일 개수')
for result in file_count:
    file = result[0]
    count = result[1]
    print('{:<7} {:>3} 개'.format(file, count))

print(f'\n프로젝트 전체 파일 수: {total_file_count} 개')
print(f'프로젝트 전체 코드 라인 수: {total_line_count} 줄\n')
