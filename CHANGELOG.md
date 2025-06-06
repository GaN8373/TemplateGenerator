# Changelog
## [3.0.0]
## features
- better foreign key support
- child directory traverser support
## commit
- docs: example comments 
- del: foreign key obsolete api del: column data getForeignKeyList api, use getForeignKeys 
- feat: supported dir tree generate; docs: add cn comments; template: add test template 
- feat: add shared context 
- template: add nullable symbol 
- refactor: ForeignKeyWithColumnData.kt rename to ForeignKeyData.kt; feat: add selected table inject; template: add efcore template 
- feat: add hasView & hasTable & getForeignKeys & getInverseForeignKeys api 
- refactor: rename to FullName 
- refactor: use context data class; del: ForeignKeyWithColumnData.getColumn func 
- feat: DbStructData add full table api 
- docs: fix changelog show in plugins "what's new" 
- docs: add changelog; add support version obsolete: foreignKey dto getColumn function 

## [2.1.0]
###  Features
- column add foreign api; 
- ForeignKeyWithColumnData add columns api
### Changed
- template add foreign example