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
      '"fulfillableQuantity": "{}", ' \
      '"inboundShippedQuantity": {}, ' \
      '"inboundWorkingQuantity": {}, ' \
      '"inboundReceivingQuantity": 0'

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
for id in "abcdefghigklmn":
    name = f.lexify(text='????????????', letters='0987654321abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
    pairs.append([id, name])


def generate_one_json_row():
    pair = f.random_element(pairs)

    r = row.format(
        f.random_element(sellers),
        pair[0],
        pair[0],
        pair[1],
        str(random.randint(1, 500)),
        str(random.randint(1, 200)),
        str(random.randint(1, 100)),
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

if __name__ == "__main__":
    """
    == How to use ==

    If set ROWS_A_FILE as m, then 1 GZ file is 116M, 1 day is 240*116M = 28G, 1 year is 10T

    """
    raw_file_name = "raw_events_{}.json".format("v1")
    # with gzip.open(i[0], "a", 9) as file:
    with open(raw_file_name, "w") as file:
        for r in range(ROWS_A_FILE):
            file.write(str(generate_one_json_row()))
