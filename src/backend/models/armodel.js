module.exports = (sequelize, DataTypes) => {
    let armodel = sequelize.define('armodel', {
        id:         { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
        model_name: { type: DataTypes.STRING(255), allowNull: false },
        category:   { type: DataTypes.STRING(255), unique: true, allowNull: false },
    }, { tableName: 'armodel' });

    armodel.associate = (models) => {
        armodel.hasMany(models.post, {onDelete: 'cascade', hooks: true});
    };
    
    return armodel;
};