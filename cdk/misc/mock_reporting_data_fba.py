import datetime
import random
# from province_city import language_code
import string
from datetime import timedelta
from multiprocessing import Process
from pathlib import Path

from faker import Faker
from faker.providers import address
from faker.providers import date_time
from faker.providers import python
import gzip

# pip install faker==4.0.1
# https://faker.readthedocs.io/en/master/fakerclass.html
#


file_dir = "fakeData/"
separtor = '|'
letters = string.ascii_letters

row = '"SellerId": "{}", ' \
      '"asin": "{}", ' \
      '"fnSku": "{}", ' \
      '"sellerSku": "Name of product - {}", ' \
      '"condition": "NewItem", ' \
      '"fulfillableQuantity": {}, ' \
      '"inboundShippedQuantity": {}, ' \
      '"inboundWorkingQuantity": {}, ' \
      '"inboundReceivingQuantity": 0, ' \
      '"eventTime": "{}" '

f = Faker()
f.add_provider(python)
f.add_provider(date_time)
f.add_provider(address)
Faker.seed(6789)

app_id_elements = tuple("fancy_game_" + str(t) for t in range(99))
other_type = tuple("other_" + str(t) for t in range(95))
types = ("login", "tutorial", "new_user", "payment") + other_type
m = 1000000
k = 1000
s = 128

sellers = ("SellerA", "SellerB", "SellerC")

pairs = []

# We have around 15 items in stock.
for id in "abcdefghigklmn":
    name = f.lexify(text='????????????', letters='0987654321abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
    pairs.append([id, name])


def get_random_date(from_y, from_m, from_d, to_y, to_m, to_d):
    try:
        date = f.date_time_between(start_date=datetime.datetime(from_y, from_m, from_d, 0, 0),
                                   end_date=datetime.datetime(to_y, to_m, to_d, 0, 0),
                                   tzinfo=None)
        # Thu, 01 Sep 2016 10:11:12 +0000 -- rfc2822
        # YYYY/MM/DD-HH:MM:SS.UUUU
        # return date.astimezone().strftime("%a, %d %b %Y %X %z")
        # return date.strftime("%Y/%m/%d-%X.0000")
        # 2021-01-08 20:23:47 https://docs.aws.amazon.com/athena/latest/ug/create-table.html
        return date.astimezone().strftime("%Y-%m-%d %X")
        # return date.astimezone().isoformat()
    except:
        print(">>>>>> Skip Day {}-{}".format(to_m, to_d))
        return ""


def generate_one_json_row(from_y, from_m, from_d, to_y, to_m, to_d):
    pair = f.random_element(pairs)

    r = row.format(
        f.random_element(sellers),
        "asin-" + pair[0],
        "fnSku-" + pair[0],
        pair[1],
        str(random.randint(1, 500)),
        str(random.randint(1, 200)),
        str(random.randint(1, 100)),
        get_random_date(from_y, from_m, from_d, to_y, to_m, to_d)
    )

    return "{" + r + "}" + "\n"


def generate_path(from_y, from_m, from_d, to_y, to_m, to_d):
    """
    s3://<bucket_name>/adjust/fba/date=2020-02-20/raw_events_00001.gz
    s3://<bucket_name>/adjust/fba/date=2020-02-20/raw_events_00001.parquet
    """
    # generate path
    f_d = datetime.datetime(from_y, from_m, from_d)
    t_d = datetime.datetime(to_y, to_m, to_d)
    delta = t_d - f_d
    print("time delta in days- " + str(delta.days))
    all_path = []
    for d in range(delta.days):
        log_date = f_d + timedelta(days=d)
        partion_date = "date=" + log_date.strftime("%Y-%m-%d")
        # print(partion_date)
        per_hour_file_names = [str(n).zfill(5) for n in range(24)]  # one day will have 240 files
        for hour in per_hour_file_names:
            raw_path = "fakeData/fba/{}/".format(partion_date)
            raw_file_name = "raw_events_{}.json".format(hour)
            # Make sure the paths are created.
            Path(raw_path).mkdir(parents=True, exist_ok=True)
            all_path.append((raw_path + raw_file_name, log_date))

    return all_path

ROWS_A_FILE = 45  # s=128 k = 117KB, m = 116M, if set at m, then 1 day is 240*116M = 28G, 1 year is 10T



def process_one_piece(p, a_slice):
    """
    :param a_slice: A list of (path: date)
    :return:
    """
    # a_slice = a_slice[0]
    print("================== a process is started to process {} files ==============".format(len(a_slice)))
    for i in a_slice:

        # with gzip.open(i[0], "a", 9) as file:
        print("i")
        print(">>>>>>>>>>>>>>>>>>>>>>> " + i[0])
        with open(i[0], "w") as file:
            print("process " + str(p) + ", write file: " + i[0] + ", for date " + str(i[1].day))
            d = i[1]
            for r in range(ROWS_A_FILE):
                file.write(str(generate_one_json_row(d.year, d.month, d.day, d.year, d.month, d.day + 1)))
    print("completed one.")


def process_one_piece_gz(p, a_slice):
    """
    :param a_slice: A list of (path: date)
    :return:
    """
    # a_slice = a_slice[0]
    print("================== a process is started to process {} files ==============".format(len(a_slice)))
    for i in a_slice:

        with gzip.open(i[0], "a", 9) as file:
            print("process " + str(p) + ", write file: " + i[0] + ", for date " + str(i[1].day))
            d = i[1]
            for r in range(ROWS_A_FILE):
                file.write(generate_one_json_row(d.year, d.month, d.day, d.year, d.month, d.day + 1).encode("utf-8"))
    print("completed one.")


if __name__ == "__main__":
    """
    == How to use ==

    If set ROWS_A_FILE as m, then 1 GZ file is 116M, 1 day is 240*116M = 28G, 1 year is 10T

    """
    # Time span will decide how much log will be generated.
    all_path = generate_path(2019, 1, 1, 2019, 1, 3)
    # How many process to use.
    num_process = 8
    batch_size = len(all_path) // num_process
    remain_size = len(all_path) % num_process
    print("remain size " + str(remain_size))
    crr = 0
    for p in range(num_process):
        a_slice = all_path[crr:crr + batch_size]
        print("slice size " + str((crr, crr + batch_size)))
        crr = crr + batch_size
        ps = Process(target=process_one_piece, args=[p, a_slice])
        ps.start()
    if remain_size > 0:
        Process(target=process_one_piece, args=[p, all_path[-remain_size]]).start()
