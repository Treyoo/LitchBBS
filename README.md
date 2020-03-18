# LitchBBS

网页论坛，使用SpringBoot,MyBatis,Thymeleaf



## 开发遇到的一些问题
-获取会话列表时mysql报错
Error Code: 1055.Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'id'
是因为MySQL5.7版本后默认设置了sql_model=only_full_group_by，限制了select的字段必须全部出现在gruop by中。
mysql命令行执行以下命令即可（该命令去除了全局的only_full_group_by设置）。
set global sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
参考：https://www.cnblogs.com/gjmhome/p/11337329.html
