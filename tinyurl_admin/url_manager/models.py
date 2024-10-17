from django.db import models


class Url(models.Model):
    short_url = models.CharField(db_column='short_url', primary_key=True, max_length=255)  # Field name made lowercase.
    long_url = models.CharField(db_column='long_url', max_length=255)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'urls'
    
    def __str__(self):
        return f'{self.short_url} : {self.long_url}'
