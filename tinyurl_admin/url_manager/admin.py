from django.contrib import admin

from .models import Url

class UrlAdmin(admin.ModelAdmin):
    search_fields = ['short_url', 'long_url']
    list_per_page = 20

admin.site.register(Url, UrlAdmin)
